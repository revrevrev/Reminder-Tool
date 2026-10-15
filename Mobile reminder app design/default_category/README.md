# Handoff — Default category for new reminders (delta)

This package describes **only one change**, made on top of `design_handoff_reminders_app/`. Everything not mentioned here is unchanged; tokens, components and behaviour from that package still apply.

## Summary
The user can pick a **default category** in Settings. New reminders open with it pre-selected.

## 1. Settings (הגדרות) — new card
Placed directly **below** "משך נודניק" (snooze length), above "ארכיון".

- Card: same `.card` as the snooze card (surface bg, radius 28, padding 16×18, gap 12).
- Title "קטגוריית ברירת מחדל": 15/600, `text`.
- Subtitle 13px, `neutral-700`:
  - default set → "נבחרת מראש בכל תזכורת חדשה"
  - none → "בכל תזכורת חדשה תתבקשו לבחור קטגוריה"
- Options: wrapping chips, gap 8. First "ללא" (none, no dot), then one chip per category in category order, each with a 12dp color dot + name.
- Chip: pill (radius 999), padding 8×14, 14/600, bg `bg`.
  - Selected: inset 2dp ring `text`.
  - Unselected: no ring.
  - Ring transition 200ms.
- Single select. Tapping applies immediately; there's no save button and no toast.

## 2. New reminder sheet
- On open in **new** mode, the category chip for `defaultCategoryId` is pre-selected (same selected style as a tapped chip).
- The user can still pick another category.
- With "ללא", nothing is pre-selected and Save stays disabled until a category is picked (existing behaviour).
- **Edit** mode is unaffected: the reminder's own category is shown.

## 3. Categories tab (קטגוריות)
- The default category is listed **first**; the others keep their existing order.
- There's **no badge or marker**. The default is managed only from Settings.
- With "ללא", the order is unchanged.

## 4. Logic
- **Deleting the default category:** `defaultCategoryId` becomes the move target the user chose in the delete flow. If the category had no reminders (no move target), it becomes `null`.
- If `defaultCategoryId` points to a category that no longer exists, treat it as `null`.

## Data model
```
Settings { defaultSnoozeMinutes: 5|10|15|30,
           defaultCategoryId: categoryId | null }   // NEW
```
- Seed value: משפחה (`family`).
- Store it with the other settings (DataStore).

## Screenshots
`screenshots/`:
- 01 settings, default set
- 02 settings, "ללא"
- 03 categories, default first (default = בית)
- 04 new reminder, pre-filled

## Files
- `Reminders App.dc.html`: the updated prototype. Open it in a browser with `support.js` alongside. Use it as a design reference only; don't ship it.
