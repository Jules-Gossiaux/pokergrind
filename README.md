# PokerGrind

PokerGrind est une application Android d'entraînement au poker conçue pour
transformer les connaissances préflop en automatismes.

Le produit avance volontairement par petites étapes. La première version
entraîne uniquement les ranges d'open en cash game 6-max, à 100 BB et avec un
sizing fixe de 2,5 BB.

## Principes du produit

- apprendre un spot à la fois dans un ordre pédagogique ;
- travailler davantage les erreurs que les acquis ;
- privilégier la justesse avant la vitesse ;
- proposer une session quotidienne courte d'environ 20 questions ;
- fonctionner entièrement hors ligne, sans compte ni backend ;
- conserver une interface mobile, ludique, sobre et rapide.

## Périmètre initial

Ordre de déblocage du premier module :

1. Open BTN
2. Open CO
3. Open HJ
4. Open UTG
5. Open SB

Les défenses de blindes, 3-bets et défenses contre 3-bets appartiennent à la
suite du parcours préflop, mais ne font pas partie du premier incrément.

## Stack retenue

- Kotlin
- Jetpack Compose et Material 3
- architecture modulaire orientée fonctionnalités
- stockage local avec Room et DataStore
- coroutines et Flow
- tests unitaires Kotlin et tests d'interface Compose

La décision détaillée se trouve dans
[ADR-0001](docs/decisions/0001-android-native-stack.md).

## Documentation

- [Spécification produit](docs/product-spec.md)
- [Architecture](docs/architecture.md)
- [Modèle de données](docs/data-model.md)
- [Répétition espacée](docs/spaced-repetition.md)
- [Direction visuelle](docs/design-system.md)
- [Transcription Open BTN](docs/ranges/open-btn.md)
- [Feuille de route](docs/roadmap.md)
- [Décisions d'architecture](docs/decisions/README.md)
- [Historique des changements](CHANGELOG.md)

## Sources des ranges

Les images placées dans `ranges/` sont des références locales fournies par le
propriétaire du projet. Elles sont ignorées par Git et ne seront jamais
affichées ou embarquées telles quelles dans l'APK.

Chaque range sera retranscrite en données structurées, contrôlée manuellement
et couverte par des tests avant son utilisation dans l'application.

## Développement local

Prérequis :

- JDK 17 ;
- Android SDK 36 ;
- Android Studio pour gérer le SDK et l'émulateur, même si le code est édité
  dans VS Code.

Commandes principales sous PowerShell :

```powershell
./gradlew test
./gradlew assembleDebug
```

L'APK de debug est généré dans `app/build/outputs/apk/debug/`.

## État du projet

La première tranche verticale Open BTN est disponible : accueil, chemin de
progression Fondations, session persistante et équilibrée de 20 questions,
XP, série quotidienne, historique Room, maîtrise sur les 30 dernières
réponses guidées, répétition espacée, entraînement libre ciblé, déblocage
d'Open CO, chronomètre informatif, feedback Open/Fold et matrice native de la
range.
