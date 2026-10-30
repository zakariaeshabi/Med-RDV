# Med-RDV — Gestion des rendez-vous médicaux

## Présentation

Med-RDV est un projet d’application de gestion des rendez-vous
médicaux pour un cabinet comprenant plusieurs praticiens.

Le périmètre fonctionnel prévu comprend :
- La gestion des praticiens et de leurs créneaux disponibles.
- La gestion des patients et de leurs dossiers médicaux de synthèse.
- La demande, la confirmation, la réalisation et l’annulation
  des rendez-vous.
- La consultation des allergies du patient avant la consultation.
- La mise à jour du dossier médical après la consultation.

L’application repose sur trois services métier indépendants :
- `service-praticiens`
- `service-patients`
- `service-rendezvous`

Un serveur Eureka et une API Gateway assurent l’enregistrement,
la découverte des services et le routage dynamique des requêtes.

## Technologies

Java 17, Spring Boot 3.5.6, Spring Cloud 2025.0.0,
Spring Web, Spring Data JPA, H2, Eureka,
Spring Cloud Gateway WebFlux et Maven.

## Diagramme de classes

Le diagramme présente le modèle métier prévu, les relations
internes et les dépendances entre services.

![Diagramme de classes](diagramme-class.png)

## Architecture — Jalon 2

Le projet comprend cinq applications Spring Boot :
trois services métier, un serveur Eureka et une API Gateway.

![Architecture Med-RDV](architecture-app.png)

| Application | Port |
|-------------|------|
| `eureka-server` | 8761 |
| `api-gateway` | 8080 |
| `service-praticiens` | 8081 |
| `service-patients` | 8082 |
| `service-rendezvous` | 8083 |

### Schéma d’architecture

```mermaid
flowchart TB
    C["Client / Navigateur"] --> G["api-gateway : 8080"]

    G --> P["service-praticiens : 8081"]
    G --> PA["service-patients : 8082"]
    G --> R["service-rendezvous : 8083"]

    G -.->|"Découverte et enregistrement"| E["eureka-server : 8761"]
    P -.->|"Enregistrement"| E
    PA -.->|"Enregistrement"| E
    R -.->|"Enregistrement"| E
```

Les services s’enregistrent dans Eureka.
La Gateway découvre les instances disponibles et leur transmet
les requêtes sans déclaration manuelle de routes.

Exemple :
`/service-patients/api/ping` est transmis au service patients
sous la forme `/api/ping`.

## Démarrage

1. Ouvrir le projet dans IntelliJ IDEA.
2. Charger les modules Maven et leurs dépendances.
3. Démarrer le serveur Eureka.
4. Démarrer les trois services métier.
5. Démarrer l’API Gateway.
6. Vérifier les applications enregistrées sur :
   http://localhost:8761/

Ne lancer qu’une instance de chaque application sur son port.

## Tests du jalon 2

Les tests suivants sont indiqués comme validés dans la documentation
du jalon 2. Ils seront à relancer après les modifications du jalon 3.

| Service | Accès direct | Accès via Gateway | Résultat documenté |
|---------|--------------|-------------------|-------------------|
| Praticiens | http://localhost:8081/api/ping | http://localhost:8080/service-praticiens/api/ping | HTTP 200 |
| Patients | http://localhost:8082/api/ping | http://localhost:8080/service-patients/api/ping | HTTP 200 |
| Rendez-vous | http://localhost:8083/api/ping | http://localhost:8080/service-rendezvous/api/ping | HTTP 200 |

Exemple de réponse :

```json
{
  "status": "OK",
  "service": "service-rendezvous"
}
```

Ces tests valident l’accès aux endpoints de test et le routage
de la Gateway. Ils ne remplacent pas les tests des API métier.

## Démonstration de découverte dynamique

### Procédure

1. Garder Eureka et la Gateway démarrés.
2. Arrêter puis relancer uniquement le service rendez-vous.
3. Attendre son enregistrement et sa découverte.
4. Tester :
   http://localhost:8080/service-rendezvous/api/ping

### Résultat attendu

L’appel retourne HTTP 200 sans modification des routes
ni redémarrage de la Gateway.

La preuve de cette manipulation reste à documenter.

## État d’avancement

### Réalisé selon la documentation du jalon 2

- Description du projet et découpage en trois services métier.
- Diagramme de classes et schéma d’architecture.
- Création et démarrage des trois modules métier.
- Mise en place du serveur Eureka.
- Configuration de l’enregistrement des services dans Eureka.
- Mise en place de la Gateway avec routage dynamique.
- Validation des trois endpoints `/api/ping` en accès direct.
- Validation des trois endpoints `/api/ping` via la Gateway.

### Vérifications et preuves à compléter

- Conserver une capture Eureka avec les quatre applications
  clientes en statut `UP`.
- Documenter la démonstration de découverte dynamique.
- Vérifier les API métier existantes via la Gateway.

### Développement métier à compléter ou à valider

- Entités et repositories.
- Couches Service et Controller métier.
- DTO validés et gestion des erreurs.
- Tests des fonctionnalités métier avec Postman.

## Livraison — Jalon 2

Le jalon 2 porte sur :
- Un serveur Eureka opérationnel.
- L’enregistrement et la découverte des services métier.
- Une API Gateway avec routage dynamique.
- Un schéma d’architecture indiquant les noms et ports.
- Une version du projet personnel publiée sur Git avec un tag.

Le tag `v2.0` est déjà publié dans le dépôt.
Les corrections ultérieures sont conservées dans l’historique Git.

Le dépôt à remettre est Med-RDV, et non le dépôt d’entraînement
e-commerce du TP.
## Jalon 5

[Guide et architecture des notifications](docs/jalon5.md).
