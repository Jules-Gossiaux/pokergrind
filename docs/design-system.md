# Direction visuelle

## 1. Intention

PokerGrind doit évoquer un jeu d'apprentissage quotidien, pas un solveur. Le
ton visuel est chaleureux, calme et direct : grandes surfaces, peu de bruit,
typographie affirmée et interactions faciles à une main.

Les captures de référence servent d'inspiration de palette et de rythme, sans
être reproduites à l'identique.

## 2. Palette initiale

Palette proposée pour le thème sombre prioritaire :

| Rôle | Couleur | Hex |
|---|---|---|
| Fond principal | charbon chaud | `#171816` |
| Surface | gris olive très sombre | `#232520` |
| Surface élevée | pierre sombre | `#30322D` |
| Texte principal | blanc cassé | `#F4F1EA` |
| Texte secondaire | gris chaud | `#AAA9A2` |
| Action principale | vert doux | `#78BE7B` |
| Succès | vert | `#65B66A` |
| Erreur | corail doux | `#E47467` |
| Progression / XP | pêche | `#EDB87A` |
| Accent range | bleu clair | `#79B9EB` |

Les contrastes devront être vérifiés selon WCAG avant stabilisation.

## 3. Formes et interactions

- boutons principaux larges, accessibles au pouce ;
- coins généreusement arrondis ;
- cartes et feuilles modales pour les détails ;
- animations brèves et utiles après une réponse ;
- vibration légère optionnelle pour le feedback ;
- aucune information essentielle transmise uniquement par la couleur.

## 4. Écran d'entraînement

Hiérarchie proposée :

1. progression de la session ;
2. contexte du spot ;
3. main en très grand ;
4. temps discret ;
5. deux grands boutons d'action ;
6. feedback compact et bouton `Voir la range`.

La matrice de range est reconstruite en Compose à partir des 169 entrées. Elle
doit rester lisible en plein écran et permettre d'identifier la main courante.

## 5. Accessibilité

- zones tactiles d'au moins 48 dp ;
- taille de texte adaptable ;
- libellés explicites pour les lecteurs d'écran ;
- contraste vérifié dans les états normal, succès, erreur et désactivé ;
- réduction des animations respectée si demandée par le système.
