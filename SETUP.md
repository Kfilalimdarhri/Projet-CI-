# Setup CI via Docker

Tout tourne en conteneur. Seul prérequis : **Docker Desktop**.

## 1. Démarrer Jenkins + SonarQube

```bash
docker compose up -d --build
```

- Jenkins : http://localhost:8081 (8080 est déjà pris par pgAdmin)
- SonarQube : http://localhost:9000 (login par défaut `admin` / `admin`)

Les deux sont sur le même réseau `ci`, donc Jenkins joint SonarQube via
`http://sonarqube:9000` (déjà configuré dans le `Jenkinsfile`).

## 2. Déverrouiller Jenkins

Mot de passe initial :

```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Coller dans Jenkins → **Install suggested plugins**.
Puis installer en plus (Manage Jenkins → Plugins) : **SonarQube Scanner**.

## 3. Configurer les outils (Manage Jenkins → Tools)

Décocher "Install automatically" et pointer vers les chemins déjà présents dans l'image :

| Outil  | Nom (exact)  | Chemin                 |
|--------|--------------|------------------------|
| JDK    | `JDK 17`     | `/opt/java/openjdk`    |
| Maven  | `Maven`      | `/opt/maven`           |

(Les noms doivent matcher le bloc `tools { }` du Jenkinsfile.)

## 4. Connecter SonarQube

1. Dans SonarQube → My Account → Security → générer un **token**.
2. Dans Jenkins → Manage Jenkins → Credentials → Add → **Secret text**
   - ID : `sonar-token`
   - Secret : le token

## 5. Créer le job

Nouveau item → **Pipeline** → section Pipeline :
- *Pipeline script from SCM* (Git) si le projet est sur GitHub, **ou**
- coller le contenu du `Jenkinsfile` directement (remplacer alors `checkout scm` par rien).

Lancer **Build Now**.

## Preuve Docker (bonus)

Le pipeline construit l'image `epsi/bad-practices-app:latest`. Pour la capture du rapport :

```bash
docker run -d --name bad-app epsi/bad-practices-app:latest
docker ps
```

## Dépannage

- SonarQube ne démarre pas (Elasticsearch) → augmenter `vm.max_map_count` :
  `wsl -d docker-desktop sysctl -w vm.max_map_count=262144`
- `docker: permission denied` dans le pipeline → le conteneur Jenkins tourne en `root`
  (déjà réglé dans `docker-compose.yml`).
