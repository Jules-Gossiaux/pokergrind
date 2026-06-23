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

Les autres spots sont conservés comme candidats mais ne sont pas codés dans
ce premier incrément.

### BB vs UTG open

3-bet value :

- `AA-QQ`
- `AKs`, `AKo`

Call :

- `JJ-22`
- `AQs-AJs`
- `KQs`
- `QJs`
- `JTs`
- `T9s`
- `98s`
- `AQo`

### BB vs HJ open

3-bet :

- `AA-QQ`
- `AKs`, `AKo`
- `AQs`

Call :

- `JJ-22`
- `AJs-ATs`
- `KQs-KJs`
- `QJs`
- `JTs`
- `T9s`
- `98s`
- `87s`
- `AQo`

### BB vs CO open

3-bet :

- `AA-TT`
- `AKs`, `AKo`
- `AQs-AJs`
- `KQs`

Call :

- `99-22`
- `ATs-A2s`
- `KJs-KTs`
- `QJs-QTs`
- `JTs-J9s`
- `T9s`
- `98s`
- `87s`
- `76s`
- `AQo-AJo`
- `KQo`

### BB vs SB open

3-bet :

- `AA-99`
- `AKs`, `AKo`
- `AQs`, `AQo`
- `AJs`, `AJo`
- `A5s-A2s`
- `KQs-KJs`

Call :

- `88-22`
- tous les As suited ;
- `KTs-K2s`
- `QTs-Q5s`
- `JTs-J7s`
- `T9s-T7s`
- `98s-86s`
- `76s-54s`
- `ATo-A2o`
- `KQo-KTo`
- `QJo-QTo`
- `JTo`

## Rappel : futur chapitre 3-bets

`SB vs BTN` appartient au futur chapitre `3-bets`, pas au chapitre
`Défenses BB`.

Stratégie candidate débutant : 3-bet ou fold uniquement.

3-bet :

- `AA-99`
- `AKs`, `AKo`
- `AQs`, `AQo`
- `AJs+`
- `KQs`

Fold :

- toutes les autres mains.

Cette range devra être normalisée avant codage, notamment pour éviter les
doublons entre `AQs`, `AKs` et `AJs+`.
