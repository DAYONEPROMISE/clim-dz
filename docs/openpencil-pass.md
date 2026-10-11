# OpenPencil pass — clim-dz

## Design (Shared/OpenPencil/clim-dz/)
- `design.html` (sizing input + result card + 4-tab nav, DZ tokens) → `design.fig`
- `lint --preset accessibility`: 0 errors, 0 warnings

## Code changes
- `MainActivity.kt`: every bottom-nav tab used the same `Check` icon with `contentDescription = null`.
  Now per-tab icons (Calculate/Person/List/Settings) with the tab label as description.
  Locale persistence + dark/light schemes untouched.

## APKs
`android-build.yml` adds `assembleRelease` (unsigned) + `app-release` artifact + a versioned
`release-v*-b*` GitHub Release for the release APK (debug flow unchanged).
