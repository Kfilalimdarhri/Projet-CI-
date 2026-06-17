pipeline {
    agent any

    // Outils à définir dans la configuration Jenkins (Manage Jenkins -> Global Tool Configuration)
    tools {
        maven 'Maven' // Correspond au nom configuré pour Maven dans Jenkins
        jdk 'JDK 17'  // Correspond au nom configuré pour le JDK 17 dans Jenkins
    }

    environment {
        // Variables d'environnement
        SONAR_HOST_URL = 'http://sonarqube:9000'
    }

    stages {
        stage('Checkout') {
            steps {
                // Récupération du code source depuis Git
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Compilation du projet...'
                sh 'mvn clean compile -DskipTests'
            }
        }

        stage('Test & Code Coverage') {
            steps {
                echo 'Exécution des tests et génération du rapport JaCoCo...'
                sh 'mvn test'
            }
            post {
                always {
                    // Publication des rapports de tests JUnit
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo 'Analyse de la qualité du code avec SonarQube...'
                // withSonarQubeEnv injecte l'URL + le token du serveur "SonarQube"
                // (configuré dans Manage Jenkins) et permet au stage Quality Gate
                // de retrouver l'analyse.
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -Dsonar.projectKey=bad-practices-app'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                // On attend le verdict du Quality Gate et on arrête le pipeline s'il échoue
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package & Docker Build') {
            steps {
                echo 'Création du JAR exécutable et de l\'image Docker...'
                sh 'mvn package -DskipTests'
                sh 'docker build -t epsi/bad-practices-app:latest .'
            }
        }
    }
}
