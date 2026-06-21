# ADR-0001 — Stack Android native

- Statut : acceptée
- Date : 2026-06-21

## Contexte

Le produit cible uniquement Android sous forme d'APK, fonctionne hors ligne et
demande une interface très fluide. Aucune contrainte multiplateforme ou web
n'existe pour le MVP.

## Décision

Utiliser :

- Kotlin ;
- Jetpack Compose et Material 3 ;
- coroutines et Flow ;
- Room pour les données d'apprentissage ;
- DataStore pour les préférences ;
- injection de dépendances légère, avec Hilt seulement si le graphe le justifie ;
- Gradle Kotlin DSL et catalogue de versions.

## Conséquences

### Positives

- accès direct aux API Android et génération d'APK standard ;
- interface déclarative adaptée aux états d'entraînement ;
- écosystème de test et de persistance mature ;
- pas de dépendance à un runtime multiplateforme.

### Coûts

- une éventuelle version iOS nécessitera un autre client ou une évolution
  d'architecture ;
- le découpage Gradle devra rester mesuré pour éviter des temps de build
  inutiles.

## Alternatives écartées

- Flutter : utile pour plusieurs plateformes, avantage absent ici ;
- React Native : ajoute une couche JavaScript sans besoin produit ;
- application web embarquée : moins naturelle pour une expérience Android
  locale et tactile.
