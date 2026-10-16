## Diagramme de classes

Le diagramme suivant présente les entités des trois services métier,
leurs relations internes et les dépendances entre services.

![Diagramme de classes Med-RDV](diagramme-class.png)

## Architecture du projet

Le projet est composé de cinq applications Spring Boot :
trois services métier, un serveur Eureka et une API Gateway.

![Architecture Med-RDV](architecture-app.png)

### Applications et ports

| Application | Port |
|-------------|------|
| API Gateway | 8080 |
| Service Praticiens | 8081 |
| Service Patients | 8082 |
| Service Rendez-vous | 8083 |
| Serveur Eureka | 8761 |
