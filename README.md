# ClimDZ — Outil dimensionnement & maintenance clim (Algérie)

Offline-first Android app (Kotlin, Jetpack Compose Material 3, Room) pour climaticiens algériens :
dimensionnement BTU/puissance d'une pièce (surface, hauteur, exposition, isolation),
check-lists maintenance frigorifique, registre clients, fiches d'intervention avec partage PDF,
français/arabe RTL, 100 % hors-ligne, sans compte.

## Règle de dimensionnement (vérifiée)

Base : **surface(m²) × 340 BTU/m²** (plafond 2,6 m), mise à l'échelle par hauteur/2,6, puis :
- exposition : ombragé ×0,90 / normal ×1,00 / ensoleillé ×1,12
- isolation : faible ×1,15 / moyenne ×1,00 / bonne ×0,90
- +600 BTU par occupant au-delà de 2
- +4000 BTU si cuisine
- +10 % si dernier étage

Sources : règle HVAC 20 BTU/pied² (ENERGY STAR/DOE ≈ 215 BTU/m² à 2,4 m, ajustée climat chaud),
calculateurs 2026 (100–135 W/m² → ×3,412), retours marché DZ (12 000/18 000/24 000 BTU T3 tropical,
prix constatés 65 000–180 000 DA sur Ouedkniss/HomeCenterDZ). Tailles standard : 9000/12000/18000/24000/30000/36000.
Ceci est une estimation de chantier, pas un calcul Manual J complet.

## Table de test (SizingEngineTest, 8 tests unitaires)

| Cas | Entrée | BTU calculés | Modèle |
|---|---|---|---|
| Salon 20 m², 2,8 m, normal | 20/2,8/AVG/AVG/2 | 7320 | 9000 |
| Même, ensoleillé + faible + dernier étage | SUNNY/POOR/top | 10380 | 12000 |
| Ombragé + bonne isolation | SHADED/GOOD | 5930 | 9000 |
| Cuisine 15 m² (+4000) | kitchen | 5100 → 9100 | 12000 |
| 4 occupants (+1200) | occ=4 | +1200 vs base | — |
| Salon 30 m² sud, 4 pers, dernier étage | 30/2,8/SUNNY/4/top | 14850 | 18000 |
| Villa 40 m², 3 m, tout cumulé | 40/3,0/SUNNY/POOR/5/cuisine/top | 28610 | 30000 |
| Conversion | 12000 BTU | 3,52 kW (÷3412) | — |

## Build

- CI (`.github/workflows/build-debug.yml`) : `testDebugUnitTest` + `lintDebug` + `assembleDebug`, APK en artifact + release `debug-latest`.
- Local : `./gradlew :app:assembleDebug` (JDK 17, Android SDK 34).

## Limites

- Estimation simplifiée : pas de prise en compte vitrage/orientation précise, altitude, appareils électriques, infiltration.
- PDF généré via `PdfDocument` (mise en page simple) ; partage via FileProvider.
- FR par défaut, AR complet ; pas de sync/cloud par design.

## Coûts

- Dev : 0 DA (stack open-source). Compte Google Play (25 USD, une fois) si publication store — sinon distribution APK directe (WhatsApp/clé USB), 0 DA.

## Premiers 10 clients (plan)

1. 3 installateurs connus (entourage Alger/Blida) : install gratuite + fiche PDF à leur nom.
2. Groupes Facebook « climatiseurs Algérie » : vidéo 60 s de calcul sur chantier réel.
3. Grossistes (Baba Hassen, El Eulma) : QR vers l'APK au comptoir contre retours terrain.
4. Objectif : 10 installateurs actifs → 50 fiches/mois → version 1.1 avec leurs retours (prix T3, wilayas).
