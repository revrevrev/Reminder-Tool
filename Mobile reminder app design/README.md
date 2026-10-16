# Handoff: Reminders App (Android, Hebrew / RTL)

> **Updated:** see `CHANGES.md` for the latest round (Paper theme, full-screen reminder editor, minimal pass, Android insets). It overrides this file where they conflict.

## Overview
A personal reminders app for Android, fully in Hebrew with right-to-left layout. Two reminder types:
- **One-off (חד-פעמית)** — a date + time. When marked done it is **archived** and disappears from the list.
- **Weekly (שבועית)** — repeats on **one** weekday at a set time. Marking done skips to next week.

Each reminder belongs to a **category**. Categories own the default **sound** and **volume**; a reminder inherits them unless the user overrides them while editing. Reminders can be **paused** (on hold) and resumed. A full-screen **alarm** supports custom-length snooze and "done".

## About the Design Files
The files in this bundle are **design references created in HTML** — a working prototype showing intended look and behavior, not production code to copy. Recreate the design in the target codebase's environment (e.g. Jetpack Compose / Material 3, or React Native / Flutter if that's what the project uses) following its established patterns. If no codebase exists, **Kotlin + Jetpack Compose** is the recommended choice (native alarms, exact-alarm scheduling, notification channels, full-screen intents).

Open `Reminders App.dc.html` in a browser to interact with it (keep `support.js` and `_ds/` next to it). A "Prototype controls" panel beside the phone can fire the next alarm, show the empty state, and reset sample data — these are **prototype-only**.

## Fidelity
**High-fidelity.** Colors, typography, radii, spacing, copy and interactions are final. Recreate pixel-accurately, mapping the tokens below into the app's theme.

---

## Global layout
- Frame used in the mock: 360×780 dp content (380×800 incl. 10px bezel). Design for any phone width; content is single-column.
- `dir="rtl"`, `lang="he"`. All rows mirror: leading = right. Chevrons point **left** (`‹`) for "go forward". Play/resume triangles are mirrored (point left). Time/numbers are LTR inside RTL text (handled by bidi).
- Status bar on top; **bottom navigation** pinned (surface `#ebddc5`, padding 10/12/26 dp), 3 tabs:
  1. **תזכורות** (Reminders) — bell icon
  2. **קטגוריות** (Categories) — 4-circle grid icon
  3. **הגדרות** (Settings) — sliders icon
  Active tab: icon inside a pill (padding 5×16, radius 999) filled `accent-200 #ffe1d0`, label + icon `accent-800 #643312`. Inactive: transparent, `neutral-700 #645c50`. Label 12/600. The Archive screen is reached from Settings and keeps the Settings tab highlighted.
- Scrollable content has 110 dp bottom padding so it clears the nav.
- **Toast / snackbar**: floats 96 dp above bottom, inset 16 dp, pill, bg `neutral-900 #2e2b25`, text `bg #f5ead8` 14px, optional "ביטול" (Undo) text button in `accent-300 #ffc6a5`. Slides up 20dp + fades, 300ms `cubic-bezier(.2,.8,.2,1)`; auto-hides after 4 s.
- **Bottom sheets**: bg `#f5ead8`, top radius 32, padding 12/20/30, drag handle 40×5 `neutral-400`, max-height 93% (scrolls). Backdrop `neutral-900` at 50% opacity. Enter/exit: translateY(105%)↔0, 350ms `cubic-bezier(.2,.8,.2,1)`; backdrop opacity 300ms. Tapping backdrop closes.

---

## Screens / Views

### 1. Home — Reminders list (תזכורות)
**Purpose:** See what's next, act on it, browse all active reminders sorted by next occurrence.

Top to bottom:
1. **Header** (padding 20/24/14): date line "יום שלישי, 13 באוקטובר" 13px `neutral-700`; title "תזכורות" — heading font 32px.
2. **Category filter chips** — horizontal scroll row, padding 0 16 14, gap 8. First chip "הכל" (All), then one per category with a 10dp color dot. Chip: pill, padding 8×14, 13/600. Selected: bg `text #201e1d`, fg `bg #f5ead8`. Unselected: bg `surface #ebddc5`, fg text. Filtering affects both the hero and the list.
3. **"Up next" hero card** (first reminder after filtering) — margin 0 16, padding 22/22/20, radius 34, clipped, tap = edit.
   - Background = **the reminder's category color**; text and decoration derived from that color's ramp (see *Category tone* below). Two decorative circles anchored top-left (RTL far side): 170dp at (-40,-40) 55% opacity using tone `c1`; 70dp at left 34/top 62, 60% opacity using tone `c2`.
   - Kicker 12/600: "הבא בתור · בעוד 2 ש׳ ו-10 דק׳" (relative countdown).
   - Name: heading font 32px, line-height 1.15.
   - Meta 14px: "היום ב-18:30 · חד-פעמית" or "… · כל יום רביעי".
   - If snoozed: extra 13/600 line "נודניק עד HH:MM".
   - Actions row (margin-top 16, gap 8): **בוצע** (Done) — bg `#f5ead8`, fg tone `btnFg`, check icon; **השהיה** (Pause) — bg tone `c2`, fg tone `pauseFg`, pause icon. Buttons padding 9×16, pill.
   - **Paused hero**: bg `neutral-300 #dcd3c4`, fg `neutral-800`, circles `neutral-200`/`neutral-400`, kicker "בהשהיה · לא תופעל", single action **המשך** (Resume) bg `accent-2-600 #728157`, fg bg.
4. Section label "בהמשך" (Later) 12/600 `neutral-700`, padding 24/24/6.
5. **List rows** (container padding 0 12; row padding 11×12, radius 22, gap 14; hover/press bg `surface` / `neutral-300`; tap = edit):
   - Time column 60dp, centered: day label 11px `neutral-700` ("מחר", "ה׳ 15.10", or "כל ד׳" for weekly), time heading font 19px.
   - 4dp-wide full-height color bar = category color.
   - Name 15/600; optional sub-line "נודניק" (`accent-700`) or "בהשהיה" (`neutral-700`) 12px.
   - Weekly repeat icon 16dp, `neutral-700`.
   - Trailing 34dp round icon button: pause (active) / resume (paused), `neutral-700`, hover `neutral-300`.
   - **Paused row** stays in its sorted position, but: row bg `neutral-200 #eee7db`, name & time `neutral-500 #a19786`, color bar `neutral-300`, repeat + resume icons `neutral-500`.
6. **FAB** "+" — 62dp circle, `accent #c67139`, icon `#f5ead8` 26dp, shadow-md, positioned left 20 / bottom 100 (above nav). Opens New Reminder sheet.
7. **Filter-empty state**: card (surface, radius 28, padding 22) "אין תזכורות פעילות בקטגוריה {name}" + secondary button "הצגת הכל".

### 2. Empty state (no active reminders at all)
Padding 30/24. Illustration: 200dp circle `accent-2-200 #e1eecc` with a 90dp `accent-300` circle overlapping bottom-left, bell icon 64dp `accent-2-800` centered. Title "אין תזכורות כרגע" (h3); body "הוסיפו תזכורת חד-פעמית או שבועית, ונזכיר לכם בזמן." `neutral-700`, max-width 270; primary button "+ תזכורת חדשה". No FAB, no chips.

### 3. New / Edit reminder — bottom sheet
Title "תזכורת חדשה" / "עריכת תזכורת" (h3). Header trailing: (edit only) trash icon button `accent-700`, then close (secondary round icon button).

Fields in order (gap 16):
1. **Type tiles** — 2-column grid, gap 10. Tile: radius 24, padding 16, icon 24dp, title heading 18px, caption 12px.
   - חד-פעמית / "עוברת לארכיון בסיום" — selected: bg `accent-200`, fg `accent-900`, inset 2dp ring `accent-600`.
   - שבועית / "חוזרת ביום קבוע" — selected: bg `accent-2-200`, fg `accent-2-900`, ring `accent-2-600`.
   - Unselected: bg surface, fg text, no ring. Transition 200ms.
2. **שם התזכורת** (Name) — text input, min-height 46, 15px, placeholder "למשל: להתקשר לאמא".
3. **קטגוריה** (Category) — **required**. Wrapping chips (gap 8): pill, padding 9×14, 14/600, 12dp color dot + name. Selected: bg `#f5ead8` + inset 2dp ring `text`; unselected: surface. New reminders start with **no category selected**.
   - New mode only, helper line 12px `neutral-700` with music icon: before choice "הצליל והעוצמה נקבעים לפי הקטגוריה"; after: "יופעל עם {sound} · {vol}% (ברירת המחדל של {category})".
4. **Weekly only — יום בשבוע**: 7 round 40dp buttons א׳…ש׳ (Sun→Sat, RTL so א׳ is rightmost). Selected `accent-2-600` / fg bg; else surface.
5. Row: **תאריך** (date, one-off only; min = today) + **שעה** (time). Native pickers.
6. Summary pill: "יום חמישי, 15 באוקטובר ב-09:00" or "כל יום רביעי ב-08:00" — bg `accent-2-100`, fg `accent-2-800`, 13px, padding 8×14.
7. **Edit mode only — "צליל ועוצמה" card** (surface, radius 28, padding 14×16):
   - Row: music icon, title 600 + subtitle 12px ("לפי {cat}: {sound} · {vol}%" or "מותאם לתזכורת זו"), label "התאמה אישית", **switch** 48×28 (on: `accent`, knob to the left/end; off: `neutral-400`, knob right/start; 200ms).
   - When on: sound row (pill on bg, "צליל" … {name} ‹ → opens Sound picker) and volume slider "עוצמה · NN%".
   - Turning off clears the override (reminder inherits again).
   - **Sound/volume are never shown when creating.**
8. Notes: ghost button "+ הוספת הערה" reveals a textarea "הערות" (placeholder "פרטים נוספים…", min-height 70, radius 28). Shown expanded if the reminder already has notes.
9. **שמירה** (Save) — primary block button, padding 15, 16px. Disabled (45% opacity) until name **and** category are set; also disabled if the selected date + time is in the past for one-off reminders. Helper below 12px: "יש להזין שם ולבחור קטגוריה" / "יש להזין שם" / "יש לבחור קטגוריה".

### 4. Sound picker — bottom sheet (stacks above other sheets)
Title "בחירת צליל" or "צליל לקטגוריה {name}"; ghost "סיום" (Done) button. Rows (padding 10×12, radius 22, gap 14): radio dot 20dp (selected: border + fill `accent` with 4dp inner bg gap; else border `neutral-400`), name 600 + description 12px `neutral-700`, optional tag "ברירת מחדל · {category}" (when picking for a reminder: marks the category's sound), round secondary play button (mirrored triangle). Selected row bg `accent-100`. Tapping a row selects **and** previews at the relevant volume; play button previews only.

Sounds (id — name — description): `chime` צלצול רך ובהיר · `bell` פעמון עמוק ומהדהד · `birds` ציוץ ציפורים עולה ויורד · `pulse` פעימה רכה עדין להשכמה · `marimba` מרימבה שלושה צלילים · `beep` צפצוף קלאסי וחד. (The prototype synthesizes these with WebAudio; ship real audio files.)

### 5. Alarm (firing) — full-screen
Shown via full-screen intent / lock-screen activity.
- Background = category color; foreground/tone per *Category tone*.
- Top (padding 44/28): date label 14/600 ("היום"), time heading font 76px.
- Center 190dp: two pulsing rings 150dp in tone `c1` (`scale .6→1.6`, opacity .7→0, 2s ease-out infinite, second delayed 1s); 100dp `#f5ead8` disc with bell 44dp in tone `icon`, wiggling (rotate 0/-14/12/-8/5/0°, 1.2s infinite). Decorative 260dp circle top-right in tone `c2` at 45%.
- Name heading 32px; notes 15px if any.
- Bottom panel (margin 12, bottom 28, bg `#f5ead8`, radius 32, padding 18/18/16, gap 12):
  - "נודניק" label + stepper (− value +), value "NN דק׳" heading 18px, 1–120 min, ±1.
  - Presets row 5 / 10 / 15 / 30 (selected `accent` fill / bg text; else surface). Initial value = Settings default snooze.
  - Buttons: **נודניק** secondary (flex 1) and **בוצע** primary (flex 1.6) — label "בוצע · לארכיון" for one-offs.
- Sound loops (every ~2.6 s in prototype) until an action is taken.

### 6. Categories (קטגוריות)
Header "קטגוריות" + subtitle "צליל ועוצמה ברירת מחדל לכל קטגוריה". List of cards (`.card`: surface bg, radius 28; row padding 14×16, gap 14; hover `neutral-300`, press `neutral-400`): 40dp color circle, name 16/600, count 12px ("אין תזכורות" / "תזכורת אחת" / "N תזכורות" — active only), trailing-end sound name 14/600 + "עוצמה NN%", chevron. Last item: dashed (2dp `neutral-400`) "+ קטגוריה חדשה" tile.

### 7. Category sheet (create / edit / delete)
Header: 36dp color circle (live), title "קטגוריה חדשה" or the saved name, trash (edit only; hidden if it's the last category), close.
Form: **שם הקטגוריה** input (placeholder "למשל: עבודה"); **צבע** — 8 swatches 34dp (selected ring: 3dp bg + 5dp text); sound row "צליל ברירת מחדל" → Sound picker; slider "עוצמת ברירת מחדל · NN%"; note 13px explaining scope ("חל על N תזכורות בקטגוריה. M תזכורות מותאמות אישית ולא תושפע." or, when empty, "כל תזכורת בקטגוריה הזו תקבל…"); primary "יצירת קטגוריה" / "שמירה" (disabled without name). Edits are a **draft** — only applied on save; close discards.
**Delete flow** (replaces form inline): panel bg `accent-100`, radius 28: "למחוק את „{name}”?"; if any reminders (including archived) use it: "בקטגוריה יש N תזכורות. בחרו לאן להעביר אותן:" + chips of other categories (must choose). Buttons: secondary "ביטול", destructive "העברה ומחיקה" / "מחיקה" (bg `accent-700`, hover 800, press 900; disabled until a target is chosen when needed).

Palette (8): `accent-500 #d67f48`, `accent-2-500 #8fa073`, `accent-300 #ffc6a5`, `neutral-600 #82796a`, `accent-2-300 #ccdbb2`, `accent-700 #8c491a`, `accent-2-700 #56633f`, `neutral-400 #c0b6a5`. A new category defaults to the first unused color, sound `chime`, volume 70.

### 8. Settings (הגדרות)
Cards (gap 12):
- **משך נודניק ברירת מחדל** — 4 equal buttons 5/10/15/30 "דק׳" (selected `accent`).
- **ארכיון** row (bottom) — archive icon, title, subtitle "N תזכורות חד-פעמיות שהושלמו" / "ריק", chevron → Archive. Deliberately low-prominence: the archive is a rarely-used escape hatch.

### 9. Archive (ארכיון)
Ghost back button "› הגדרות", title "ארכיון", subtitle "תזכורות חד-פעמיות שהושלמו". Cards sorted by archive time desc: 12dp category dot, name with strikethrough (`neutral-500` line), meta "{date time} · הושלם {day}", secondary "שחזר" (Restore), trash icon `accent-700` (permanent delete, with undo toast). Empty: "הארכיון ריק. כשמסמנים תזכורת חד-פעמית כ„בוצע”, היא עוברת לכאן."

---

## Interactions & Behavior
- **Sorting:** all non-archived reminders (active **and** paused) by next occurrence (`snoozeUntil` if set, else computed occurrence). Paused items keep their place; they just render grey and never fire.
- **Next occurrence (weekly):** next `day` at `time` strictly after now; if `skipUntil` is set, advance by weeks until after it.
- **Mark done:** one-off → `archived=true, archivedAt=now`, toast "הועבר לארכיון" + Undo. Weekly → `skipUntil = this occurrence`, toast "נתראה ביום {day} הבא" + Undo. Clears snooze.
- **Snooze:** `snoozeUntil = currentOccurrence + N min`; toast "נודניק עד HH:MM" + Undo. Row shows "נודניק".
- **Pause / resume:** toggles `paused`, clears snooze; toast "„{name}” בהשהיה" / "„{name}” חזרה לפעול" + Undo. Resuming a one-off whose time has passed opens the editor with date = tomorrow and toast "המועד עבר — בחרו מועד חדש". Cancel the scheduled OS alarm while paused.
- **Restore from archive:** un-archives, navigates Home and opens the editor (past date bumped to tomorrow).
- **Delete reminder** (editor trash / archive trash): immediate, toast + Undo.
- **Undo** restores the previous reminders snapshot (single level).
- **Effective sound/volume:** `reminder.sound ?? category.sound`, `reminder.volume ?? category.volume`. Category changes propagate to all inheriting reminders.
- **Validation:** reminder needs non-blank name + category; category needs non-blank name. Cannot delete the last category. For **one-off (חד-פעמית)** reminders, the selected date + time must not be in the past — Save is disabled if it is.
- **Hover/press:** tints from the ramps (see components); keyboard focus = 2dp `accent` outline, 2dp offset.

## State Management
```
Reminder { id, name, type: 'once'|'weekly', date: 'YYYY-MM-DD' (once), day: 0–6 Sun..Sat (weekly),
           time: 'HH:MM', cat: categoryId, sound: soundId|null, volume: 0–100|null, notes,
           paused?: bool, snoozeUntil?: DateTime, skipUntil?: DateTime,
           archived?: bool, archivedAt?: DateTime }
Category { id, name, color (palette token), sound: soundId, volume: 0–100 }
Settings { defaultSnoozeMinutes: 5|10|15|30 }
UI       { screen: home|cats|settings|archive, filter: categoryId|null,
           reminderDraft + isEdit, categoryDraft + deleteMode + moveTarget,
           soundPickerTarget: 'reminder'|'category'|null, firingAlarmId, snoozeMinutes, toast }
```
Persist reminders, categories and settings locally (Room/DataStore). Schedule exact alarms (`AlarmManager.setAlarmClock` / `setExactAndAllowWhileIdle`) for each non-paused, non-archived reminder; reschedule on edit, snooze, done, category change, boot. Use a notification channel per category if per-category sound must work from notifications.

**Seed categories:** משפחה (accent-500, chime, 70) · בית (accent-2-500, birds, 60) · בריאות (accent-300, marimba, 50) · כספים (neutral-600, bell, 80).

## Notifications
When an alarm fires as an OS notification (e.g. when the full-screen intent is not shown):
- **Title:** the reminder's name.
- **Body:** the reminder's category name.

(Previously hardcoded to "תזכורת" / "יש לך תזכורת".)

## Category tone (derived colors on category-colored surfaces)
Given a palette color `{family}-{step}`:
- `light` = step ≤ 400
- `fg` = light ? `{family}-900` : `#f5ead8`
- `c1` = `{family}-{step-100}` · `c2` = `{family}-{step+100}`
- `pauseFg` = same as `fg` · `btnFg` = `{family}-800` · `icon` = `{family}-700`

## Design Tokens (Organic design system)
**Colors** — bg `#f5ead8` · surface `#ebddc5` · text `#201e1d` · accent `#c67139` · accent-2 `#7a8a5e`
- neutral 100–900: `#f9f4ed #eee7db #dcd3c4 #c0b6a5 #a19786 #82796a #645c50 #474238 #2e2b25`
- accent 100–900: `#fff2eb #ffe1d0 #ffc6a5 #f6a06b #d67f48 #b2622d #8c491a #643312 #402310`
- accent-2 100–900: `#f0fae1 #e1eecc #ccdbb2 #aebf92 #8fa073 #728157 #56633f #3d472b #272e1b`

**Type** — Headings: Caprasimo (Latin/digits) → **Suez One** (Hebrew fallback), weight 400. Body: Figtree → **Rubik** (Hebrew) 400/500/600/700. Sizes used: 76 (alarm time), 32 (screen titles, hero/alarm names), h3 (sheet titles, from DS), 19 (row time), 18 (tile titles, stepper), 16, 15, 14, 13, 12, 11.

**Spacing** — 4.4 / 8.8 / 13.2 / 17.6 / 26.4 / 35.2 (DS scale); the mock mostly uses literal 6–24 dp paddings as noted per component.

**Radii** — sm 8 · md 16 · lg 28 · pills 999 · hero 34 · sheets 32 (top) · alarm panel 32 · rows 22 · type tiles 24.

**Shadows** — sm `0 1px 2px rgba(46,43,37,.14)` · md `0 3px 10px rgba(46,43,37,.16)` · lg `0 12px 32px rgba(46,43,37,.22)`.

**Components** (from DS `styles.css`): `.btn` (pill; `-primary` accent fill, hover accent-600, press accent-700; `-secondary` surface; `-ghost`; `-icon` round), `.tag`, `.field`/`.input` (pill inputs), `.card` (surface, radius-lg). Disabled = 45% opacity.

## Assets
- **Icons:** Lucide, stroke-width **2.75**, round caps/joins: bell, archive, sliders-horizontal, plus, check, pause, play (mirrored), repeat, calendar, music, volume-1, trash-2, x, chevron-left/right, alarm-clock. Category tab uses a custom 4-circle grid.
- **Fonts:** Google Fonts — Caprasimo, Figtree, Suez One, Rubik.
- **Sounds:** placeholders synthesized in-browser; replace with real audio assets (6 listed above).
- No raster imagery.

## Screenshots
`screenshots/` — reference captures of the prototype (the phone frame; the panel beside it is prototype-only):
01 home · 02 home filtered by category · 03 new reminder · 04 edit reminder with sound/volume override · 05 sound picker · 06 alarm · 07 categories · 08 category edit · 09 category delete (move reminders) · 10 new category · 11 settings · 12 archive · 13 empty state.

## Files
- `Reminders App.dc.html` — the full interactive prototype (markup + logic class at the bottom of the file; data model, seed data and all behaviors live there).
- `support.js` — prototype runtime needed to open the HTML (not for production).
- `_ds/organic-…/styles.css` — Organic design-system tokens and component classes (source of truth for token values).
- `_ds/organic-…/_ds_bundle.js` — design-system bundle loaded by the prototype.
