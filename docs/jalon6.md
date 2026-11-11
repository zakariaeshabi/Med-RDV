# Jalon 6 - Resilience, erreurs et idempotence

## Adaptation a Med-RDV

Le service rendez-vous protege les lectures Feign vers :
- service-patients ;
- service-praticiens.

ResilientRequests est un bean Spring distinct du service metier.
Il applique Retry, CircuitBreaker et Bulkhead aux appels distants.
Les annotations sont activees avec Spring AOP.

## Configuration

Les proprietes sont centralisees dans :
config-repo/service-rendezvous.properties

- Connexion Feign : 1000 ms.
- Lecture Feign : 2000 ms.
- Retry : jusqu'a 3 tentatives au total, attente de 500 ms.
- Exceptions retentees : RetryableException et ServiceUnavailable Feign.
- Circuit : fenetre de 6 appels, minimum 4, seuil d'echec 50 %.
- Attente OPEN : patients 10 s ; praticiens 5 s.
- HALF_OPEN : 3 appels autorises.
- Bulkhead : 10 appels simultanes par service, sans attente.
- RateLimiter consultation : 5 permissions par seconde, sans attente.

Le circuit peut refuser une tentative : trois tentatives ne signifient
donc pas toujours trois appels reseau.

Les erreurs Feign 4xx et les refus Bulkhead sont ignores par les
circuits configures. Un 404 n'est pas transforme en donnees fictives.

La propriete bulkhead-aspect-order a ete retiree car elle empechait
le demarrage avec la version des dependances utilisee.

## Strategies de repli

Creation :
- verification distante obligatoire ;
- indisponibilite couverte par le repli : HTTP 503 ;
- aucun enregistrement metier ni demande de notification sur ce refus.

Consultation :
- donnees locales du rendez-vous conservees ;
- objet patient ou praticien a null si son service est indisponible ;
- aucun cache Caffeine dans cette version.

Quota de consultation atteint :
- HTTP 200 pour un rendez-vous existant ;
- donnees locales seules ;
- patient et praticien a null ;
- aucun appel Feign depuis ce repli.

Le RateLimiter est applique dans RendezvousService.
Il ne s'agit PAS d'un Rate Limiter sur la Gateway.
Son quota est local a l'instance de rendez-vous.

## Erreurs et idempotence

GlobalExceptionHandler centralise les erreurs :
- refus metier : 400 ;
- reference distante introuvable : 404 ;
- indisponibilite couverte par le repli : 503 ;
- conflit d'integrite : 409 ;
- autre erreur HTTP Feign : 502.

Une cle deja utilisee est refusee avec :
400 - Operation deja traitee

La contrainte unique en base protege aussi les creations concurrentes.
Cette strategie refuse les doublons ; elle ne rejoue pas la reponse initiale.
Le test sequentiel ne constitue pas un test de concurrence.

## Resultats observes pendant la session locale

- Creation et consultation enrichie : reussies.
- Rejeu d'une creation : 400, Operation deja traitee.
- Panne patients : patient null en consultation.
- Creation pendant panne patients : 503, message explicite.
- Reprise patients : enrichissement de nouveau disponible.
- Panne et reprise praticiens : declarees reussies pendant la session.
- RateLimiter : 20 reponses HTTP 200 :
  5 enrichies, 15 locales seules, aucune autre reponse.
- Logs du quota observes.

Preuves RateLimiter :
docs/preuves-v6/ratelimiter-*.json

## Verifications non encore documentees

- Compte precis des tentatives Retry et transitions du Circuit Breaker.
- Test de saturation du Bulkhead.
- Executions et captures de la nouvelle collection Postman.
- Chargement des proprietes V6 depuis le depot Git distant.

Ces points ne sont pas declares valides par les seuls tests de repli.

## Lancement : test local

Eureka, Docker (RabbitMQ et Mailpit), Config Server, puis services.

Pendant les essais, Config Server a ete lance avec :
--spring.profiles.active=native
--spring.cloud.config.server.native.search-locations=file:///C:/Users/zakar/Med-RDV/config-repo/
--spring.cloud.config.server.native.add-label-locations=false

Ce mode est temporaire et lit les fichiers locaux non publies.

## Lancement : livraison Git

Apres publication de la branche ou du tag :
1. Redemarrer Config Server normalement, sans profil native.
2. Verifier sa reponse pour le service et la version Git voulue.
3. Lancer rendez-vous avec la meme version de configuration.

Pour une branche contenant un slash, la representation du label
dans une URL Config Server necessite un encodage adapte ; utiliser
le tag v6.0 pour la demonstration finale une fois ce tag publie.

Ne pas utiliser le tag v5.0 pour charger les proprietes V6.
La verification de la configuration distante reste obligatoire.

## Collection Postman

Importer postman/jalon6.postman_collection.json.

Executer dans l'ordre :
1. Preparation : creation patient et praticien.
2. Creation rendez-vous.
3. Consultation enrichie.
4. Rejeu exact de la creation.

Les requetes de panne sont manuelles :
- arreter uniquement le service indique ;
- consulter ;
- tenter une nouvelle creation avec une nouvelle cle ;
- redemarrer le service et restaurer les donnees de test si necessaire.

Ne pas lancer automatiquement les scenarios normaux et de panne ensemble.

## Limites

- H2 en memoire : perte de donnees au redemarrage.
- Pas de cache distant ni persistant.
- Pas de preuve de livraison email exactement une fois.
- Mecanisme RabbitMQ apres commit du jalon 5 conserve.
- Tests de compilation precedents effectues avec -DskipTests :
  ils ne constituent pas une suite complete de tests automatises V6.