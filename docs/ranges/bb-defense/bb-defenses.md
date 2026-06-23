# Transcription — Défenses BB 100 BB

- Identifiants :
  - `bb_vs_btn_defense_100bb_v1`
  - `bb_vs_co_defense_100bb_v1`
  - `bb_vs_sb_defense_100bb_v1`
  - `bb_vs_hj_defense_100bb_v1`
  - `bb_vs_utg_defense_100bb_v1`
- Source : `docs/decisions/0005-bb-defense-ranges.md`
- Sizing adverse : open 2,5 BB
- Actions principales : `Call`, `3-bet` ou `Fold`
- Statut : transcrites et vérifiées automatiquement

## Résultat

| Spot | Combinaisons 3-bet | Combinaisons Call | Défense totale |
|---|---:|---:|---:|
| BB vs BTN | 70 | 204 | 274 / 1 326 |
| BB vs CO | 58 | 160 | 218 / 1 326 |
| BB vs SB | 108 | 354 | 462 / 1 326 |
| BB vs HJ | 38 | 108 | 146 / 1 326 |
| BB vs UTG | 34 | 100 | 134 / 1 326 |

## Ordre pédagogique

1. BB vs BTN
2. BB vs CO
3. BB vs SB
4. BB vs HJ
5. BB vs UTG

Cet ordre privilégie d'abord les spots fréquents et importants, puis les
positions plus serrées.

## Frontières contrôlées

Quelques mains de frontière verrouillées par les tests :

| Spot | Main | Action |
|---|---|---|
| BB vs BTN | TT | 3-bet |
| BB vs BTN | KJo | Call |
| BB vs BTN | A9o | Fold |
| BB vs CO | AJs | 3-bet |
| BB vs CO | 76s | Call |
| BB vs CO | K9s | Fold |
| BB vs SB | A2s | 3-bet |
| BB vs SB | K2s | Call |
| BB vs SB | Q4s | Fold |
| BB vs HJ | AQs | 3-bet |
| BB vs HJ | AJs | Call |
| BB vs HJ | AJo | Fold |
| BB vs UTG | AKo | 3-bet |
| BB vs UTG | AQo | Call |
| BB vs UTG | ATs | Fold |

## Validation

Les tests vérifient :

- la présence des 169 catégories canoniques pour chaque spot ;
- les combinaisons `Call` et `3-bet` ;
- les frontières listées ci-dessus ;
- l'ordre pédagogique exposé dans le chapitre.
