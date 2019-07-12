pipeline {
  triggers {
    githubPush()
    pollSCM('H/15 * * * *')
  }
  parameters {
    booleanParam(
          name: 'CLEAN_WORKSPACE',
          defaultValue: false,
          description: 'Start with empty workspace'
      )
  }
  agent {
    kubernetes {
      inheritFrom 'toolbox'
      label 'pod'
      containerTemplate(
        name: 'buildbox',
        image: 'openjdk:8-jdk',
        ttyEnabled: true,
        command: 'cat'
      )
    }
  }
  stages {
    stage('Init') {
      steps {
        script {
          if (params.CLEAN_WORKSPACE) {
            echo "Wiping out workspace"
            deleteDir()
          } else {
            echo 'Skipping cleanup due to user setting'
          }
        }
      }
    }
    stage('Checkout') {
      steps {
        checkout scm
      }
    }
    stage('Compile') {
      steps {
        container('buildbox') {
          sh script: './gradlew clean build allureReport'
        }
      }
    }
    stage('Build and Push') {
      steps {
        container('toolbox') {
          script {
            final image = hub.explain(state: params.APP_STATE_FILE).stackOutputs['application.docker.image'] as String
            sh script: "docker build --pull --rm -t ${image}:latest -t ${image}:${gitscm.shortCommit} ."
            withAWS(role:params.AWS_TRUST_ROLE) {
              sh script: ecrLogin()
              sh script: "docker push ${image}:${gitscm.shortCommit}"
              sh script: "docker push ${image}:latest"
            }
          }
        }
      }
    }
    stage('Deploy') {
      environment {
        HUB_COMPONENT = 'spring-boot'
      }
      steps {
        container('toolbox') {
          script {
            sh script: "hub kubeconfig ${params.APP_STATE_FILE} --switch-kube-context"
            final version = "${gitscm.shortCommit}"
            final outputs = hub.explain(state: params.APP_STATE_FILE).stackOutputs
            final namespaceExists = sh script: "kubectl get namespace ${outputs['application.namespace']}", returnStatus: true
            if (namespaceExists != 0) {
              sh script: "kubectl create namespace ${outputs['application.namespace']}"
            }
            final kubectl = "kubectl -n ${outputs['application.namespace']}"
            hub.render(template: 'kubernetes.yaml.template', state: params.APP_STATE_FILE)
            echo readFile('kubernetes.yaml')
            sh script: "${kubectl} apply --force --record -f kubernetes.yaml"
          }
        }
      }
    }
    stage('Validate') {
      steps {
        container('toolbox') {
          script {
            final appUrl = hub.explain(state: params.APP_STATE_FILE).stackOutputs['application.url'] as String
            retry(30) {
              sleep time: 3, unit: 'SECONDS'
              final resp = httpRequest url: "${appUrl}"
              echo resp.content
              assert resp.status == 200
            }
          }
        }
      }
    }
  }
  post {
    always {
      junit(
        testResults: './tests-*.xml',
        allowEmptyResults: true
      )
      archiveArtifacts(
        artifacts: 'build/libs/*.jar',
        allowEmptyArchive: true,
        fingerprint: true
      )
      publishHTML(target: [
              reportDir: 'build/reports/allure-report',
              reportFiles: 'index.html',
              reportName: 'Allure',
              keepAll: true
      ])
    }
  }
}
