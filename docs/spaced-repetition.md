# Répétition espacée

## 1. But

Le moteur doit maximiser l'apprentissage dans une session courte :

- une erreur revient rapidement ;
- une connaissance stable revient progressivement moins souvent ;
- les mains jamais vues et les zones insuffisamment couvertes restent visibles ;
- la sélection conserve de la diversité.

L'algorithme précis sera validé par des tests de simulation avant d'être figé.

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

## 6. Entraînement libre

L'entraînement libre réutilise les mêmes états d'apprentissage, mais sans
budget quotidien. Il favorise le spot choisi et mélange :

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

## 8. Paramètres encore à calibrer

Avant implémentation définitive :

- pondération entre retard, fragilité et couverture ;
- seuils de couverture ;
- comportement lorsqu'un spot perd sa maîtrise.

Ces constantes seront centralisées, nommées et testées. Elles ne seront pas
disséminées dans l'interface.
