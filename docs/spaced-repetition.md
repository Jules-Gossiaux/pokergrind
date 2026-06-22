# Répétition espacée

## 1. But

Le moteur doit maximiser l'apprentissage dans une session courte :

- une erreur revient rapidement ;
- une connaissance stable revient progressivement moins souvent ;
- les mains jamais vues et les zones insuffisamment couvertes restent visibles ;
- la sélection conserve de la diversité.

L'algorithme actuel est volontairement simple et reste calibrable à partir de
l'usage réel.

## 2. Unité de révision

L'unité est le couple `spot + catégorie de main`. Connaître `A8s` au BTN ne
signifie pas connaître `A8s` au CO.

## 3. États

Un item suit un parcours simple :

```text
Nouveau → Apprentissage → Révision
              ↑             │
              └── erreur ───┘
```

- **Nouveau** : jamais présenté ;
- **Apprentissage** : acquis encore fragile, revu dans la session ou bientôt ;
- **Révision** : réponse consolidée, intervalle exprimé en jours.

## 4. Effet d'une réponse

### Erreur

- remet l'item en apprentissage ;
- programme une réapparition environ trois questions plus tard dans une
  session guidée ;
- augmente fortement sa priorité ;
- chaque nouvelle erreur sur la même main augmente encore cette priorité,
  jusqu'à un plafond de sécurité ;
- raccourcit sa prochaine échéance.

### Bonne réponse

- augmente la stabilité ;
- espace progressivement la prochaine révision à 1, 3, 7, 14 jours, puis
  double l'intervalle ;
- n'empêche pas une sélection liée à la couverture du spot.

Le temps de réponse est enregistré mais ne modifie pas l'intervalle dans le
MVP.

### Entraînement libre

- une erreur remet la main due immédiatement et lui applique une priorité
  supérieure ;
- une bonne réponse ne modifie pas l'échéance et ne certifie pas la maîtrise ;
- les deux résultats restent dans l'historique libre et une bonne réponse
  rapporte de l'XP.

## 5. Construction d'une session quotidienne

La session cible 20 questions. Le sélecteur remplit ce budget dans cet ordre :

1. erreurs à revoir dans la session ;
2. révisions échues, les plus en retard d'abord ;
3. mains nouvelles nécessaires à la progression ;
4. mains nécessaires à la diversité ou à la couverture ;
5. consolidation d'items fragiles.

Les révisions dues qui dépassent le budget sont reportées. Le sélecteur évite
de présenter deux fois de suite la même main, sauf absence d'alternative.

Dans chaque groupe d'action, une révision arrivée à échéance passe avant une
main jamais vue. Une erreur possède en plus un bonus de priorité nettement
supérieur. L'équilibrage conserve toutefois dix décisions Open et dix Fold et
un minimum par spot : si plus de 20 révisions sont prioritaires, le surplus est
reporté à la session suivante.

## 6. Entraînement libre

L'entraînement libre propose un bloc équilibré de 20 questions sur le spot
choisi. Il mélange :

- erreurs récentes ;
- mains fragiles ;
- mains peu rencontrées ;
- échantillon représentatif de la range.

## 7. Maîtrise et couverture

La fenêtre de maîtrise contient les 30 dernières réponses du spot. Elle exige :

- au moins 27 réponses correctes ;
- une représentation répétée de chaque action disponible ;
- une diversité de mains suffisante pour éviter une validation par hasard.

Les seuils exacts de couverture seront déterminés après import des ranges. Une
action rare ne doit pas exiger plus de catégories qu'elle n'en contient.

## 8. Paramètres à calibrer avec l'usage

- pondération entre retard, fragilité et couverture ;
- intervalles au-delà de 14 jours ;
- seuil de fiabilité des statistiques par main.

Les seuils de maîtrise actuels sont centralisés et testés. Un spot qui perd sa
maîtrise reste accessible et redevient prioritaire, sans reverrouiller les
spots déjà débloqués.

## 9. Différence avec Anki

PokerGrind utilise une répétition espacée inspirée d'Anki, mais n'embarque ni
SM-2 ni FSRS :

- une première bonne réponse programme la main à 1 jour ;
- les suivantes passent à 3, 7, 14 jours puis doublent l'intervalle ;
- une erreur remet l'échéance à maintenant, réinitialise le palier et augmente
  le nombre d'oublis et la priorité ;
- une erreur guidée est aussi réinsérée environ trois questions plus tard ;
- une erreur libre influence la prochaine session guidée ;
- une bonne réponse libre ne modifie pas l'échéance.

L'état est persisté dans la table Room `review_items` pour chaque couple
`spot + main`. Il survit donc aux fermetures et aux mises à jour de l'APK.
