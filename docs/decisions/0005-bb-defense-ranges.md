# 0005 — Ranges de défense BB débutant NL2

Date : 2026-06-23

## Statut

Accepté pour le MVP progressif.

## Contexte

Après les ranges d'open, le prochain bloc naturel des fondations est la
défense de big blind. L'objectif reste pédagogique : construire des réflexes
solides pour un joueur débutant de NL2, pas reproduire des fréquences GTO.

Les ranges doivent donc être :

- simples à mémoriser ;
- légèrement trop tight plutôt que trop loose ;
- faciles à exécuter postflop ;
- pauvres en bluffs de 3-bet ;
- compatibles avec une seule réponse correcte par main.

## Décision

Créer un chapitre `Défenses BB` dans les Fondations, distinct du chapitre
`Opens`.

Le premier spot intégré est :

1. `BB vs BTN`, 100 BB, BTN open 2,5 BB.

Les réponses possibles sont :

- `Call` ;
- `3-bet` ;
- `Fold`.

Le spot est déverrouillé après les ranges d'open, via le même système de
progression que les autres compétences. Les erreurs en défense BB alimentent
la répétition espacée par couple `spot + main`.

## Normalisation de la notation

Les notations ambiguës sont résolues ainsi :

- `AK-AQ` signifie `AKs`, `AKo`, `AQs`, `AQo` ;
- `AJs+` signifie `AJs`, `AQs`, `AKs` ;
- `98s-86s` signifie `98s`, `87s`, `86s` ;
- `76s-65s` signifie `76s`, `65s`.

Les doublons sont ignorés : si une main apparaît déjà en `3-bet`, elle ne peut
pas aussi être `Call`.

## Range BB vs BTN

### 3-bet

- `AA-TT`
- `AKs`, `AKo`
- `AQs`, `AQo`
- `AJs`
- `KQs`

### Call

- `99-22`
- `A9s-A2s`
- `KJs-K9s`
- `QJs-Q9s`
- `JTs-J8s`
- `T9s-T8s`
- `98s`, `87s`, `86s`
- `76s`, `65s`
- `AJo-ATo`
- `KQo-KJo`
- `QJo`

### Fold

Toutes les autres mains.

## Critère de maîtrise

Le critère général reste :

- 30 dernières réponses guidées ;
- au moins 27 réponses correctes ;
- couverture minimale des actions importantes.

Pour les spots à trois actions, la couverture minimale passe à 5 mains
distinctes par action dans la fenêtre de 30 réponses. Cela évite de valider un
spot sans avoir réellement rencontré des `Call`, des `3-bet` et des `Fold`.

## Hors périmètre immédiat

Les autres spots sont documentés comme candidats mais ne sont pas codés dans
ce premier incrément :

- BB vs CO ;
- BB vs SB ;
- BB vs HJ ;
- BB vs UTG.

`SB vs BTN` appartient au futur chapitre `3-bets`, pas au chapitre
`Défenses BB`.
