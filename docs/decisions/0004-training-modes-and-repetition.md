# ADR-0004 — Séparer certification, pratique libre et répétition

- Statut : acceptée
- Date : 2026-06-21

## Contexte

Les utilisateurs doivent pouvoir travailler une position précise sans
artificiellement débloquer tout le parcours. En parallèle, une erreur observée
en pratique libre constitue un signal utile pour l'entraîneur.

## Décision

- les réponses guidées alimentent la fenêtre de maîtrise ;
- les bonnes réponses libres rapportent de l'XP, mais ne certifient pas ;
- les erreurs libres rendent la main prioritaire dans le guidé ;
- chaque état de révision appartient au couple `spot + main` ;
- une erreur guidée revient environ trois questions plus tard ;
- les intervalles corrects suivent 1, 3, 7, 14 jours puis doublent ;
- un spot débloqué reste débloqué, même s'il passe ensuite à consolider ;
- les sessions guidées restent limitées à 20 questions et peuvent être
  répétées plusieurs fois par jour.

## Conséquences

- la pratique ciblée ne permet pas de contourner la progression ;
- le mode libre peut révéler une faiblesse réelle ;
- l'ajout d'une nouvelle range alimente le même planificateur multi-spots ;
- les intervalles devront être recalibrés avec l'usage quotidien réel.
