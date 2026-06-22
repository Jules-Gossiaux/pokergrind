# Feuille de route

Cette feuille de route décrit l'ordre de travail, sans date de livraison.

## Réalisé — Socle et ranges d'open

- application Android Kotlin/Compose et thème sombre ;
- cinq ranges d'open 6-max : BTN, CO, HJ, UTG et SB ;
- validation automatique des 169 catégories et des pourcentages sources ;
- parcours progressif avec déblocages permanents ;
- sessions guidées multi-spots de 20 questions ;
- entraînement libre ciblé sur un spot débloqué ;
- répétition espacée par couple spot-main ;
- reprise des sessions guidées, XP et série quotidienne ;
- reprise indépendante et simultanée des sessions guidée et libre ;
- stockage Room et DataStore avec migrations versionnées ;
- table 6-max native et matrices de ranges reconstruites ;
- consultation d'une range depuis l'accueil ou après une réponse ;
- statistiques par spot, classements et matrice colorée par main ;
- tests unitaires, Android Lint et essais sur émulateur et téléphone.

## Prochaine étape — Stabilisation du MVP

1. recueillir et corriger les retours d'usage quotidien sur téléphone ;
2. enrichir le résumé de fin de session : spots travaillés, erreurs et
   révisions restantes ;
3. ajouter des tests Compose sur les parcours critiques ;
4. vérifier l'accessibilité et les tailles d'écran ;
5. documenter les sauvegardes et migrations locales ;
6. générer une APK release signée.

## Module suivant — Défenses de grosse blinde

Après stabilisation des opens :

1. Défense BB vs BTN ;
2. Défense BB vs CO ;
3. Défense BB vs SB.

Ce module introduira les actions `Call`, `3-bet` et `Fold`. Il devra réutiliser
le moteur de session, de répétition et de statistiques sans ajouter de logique
spécifique aux opens dans les composants communs.

## Après les défenses de BB

1. 3-bet SB vs BTN ;
2. 3-bet BB vs BTN ;
3. défense contre 3-bet ;
4. récapitulatif préflop ;
5. autres fondations postflop.
