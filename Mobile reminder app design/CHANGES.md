# Changes since the original handoff (round 2)

**These changes override `README.md` wherever they conflict.** `Reminders App.dc.html` in this folder is the updated prototype. The files in `screenshots/` were retaken and show the current design (Paper theme). The panel next to the phone is a prototype-only control panel.

## 1. Theme: "Paper" (new default)
The app moves from the warm Organic palette to a light, minimal **Paper** theme. Every token keeps the same name and role (bg, surface, text, accent, accent-2 and the 100–900 ramps), so the README's per-component references (e.g. "`accent-200`") still apply; only the values change.

- bg `#ffffff` · surface `#f2f3f5` · text `#151618` · accent `#4170cb` (blue) · accent-2 `#3e8562` (green)
- neutral 100–900: `#f5f7f9 #e7e8ea #d2d4d8 #b5b7bb #92959a #727579 #55585d #3b3d41 #24272a`
- accent 100–900: `#e4f8ff #d5e9ff #afd5ff #93b8fb #6193f1 #4170cb #2552aa #08348a #00186e`
- accent-2 100–900: `#e5feef #d7efe1 #b1e2c6 #95c4aa #60a682 #3e8562 #1d6746 #004b2c #003316`
- divider: text at 12% alpha
- Shadows: sm `0 1px 2px rgba(0,0,0,.06)` · md `0 4px 14px rgba(0,0,0,.08)` · lg `0 12px 32px rgba(0,0,0,.12)`
- **Fonts:** headings and body both use **Rubik** (headings weight 500). Caprasimo and Suez One are no longer used.
- Ramps were generated in OKLCH using lightness steps .975/.93/.87/.78/.67/.56/.46/.36/.27, at hue 260 (neutral, chroma .008), 262 (accent, chroma .15) and 160 (accent-2, chroma .09). Chroma is scaled ×0.35 for steps 100–200 and ×0.7 for steps 300–400. The hex values above are sRGB conversions of those OKLCH colors.
- The category palette and the derived tones on category-colored surfaces (README → *Category tone*) use the same ramp steps as before, now with Paper's values.
- The prototype also includes alternative themes Mist and Linen (selectable via a tweak). They are not part of the scope; implement Paper only.

## 2. New / Edit reminder: full-screen page (replaces the bottom sheet)
Goal: a fast, single-glance form where the keyboard never covers the field being typed in.
- **Container:** full screen (top 0 → bottom 0), no rounded top corners, no drag handle and no backdrop visible behind it. Enters by sliding up (translateY 105% → 0, 350ms, cubic-bezier(.2,.8,.2,1)). Padding 30/20/24, vertical gap 14. Scrolls only if content overflows (e.g. the edit override panel is expanded).
- **Header row** (gap 8), in RTL order from the right:
  1. Close: ghost round icon button, 38×38, X icon 16dp.
  2. Title "תזכורת חדשה" / "עריכת תזכורת": 14px/600, `neutral-700`. This is a small label, not a heading.
  3. (Edit only) Trash icon button, `accent-700`.
  4. **Save "שמירה"**: primary pill button, padding 9×20, 15px. It moved here from the bottom of the form, so the keyboard can never hide it. Disabled (45% opacity) until a name and a category are set; also disabled if the selected date + time is in the past for one-off reminders. The helper text that used to sit under the bottom Save button is removed.
- **Field order:**
  1. **שם התזכורת** (Name): field label plus pill input, min-height 44, **16px** (16px also prevents zoom-on-focus on mobile). **No placeholder.** `enterkeyhint="done"`. It is the first field so it sits near the top of the screen, above the keyboard.
  2. **Type selector:** two-column grid, gap 8, with compact pills (radius 999, padding 9×12, 14px/600, 16dp icon + label, centered). Labels are only "חד-פעמית" and "שבועית"; the caption lines ("עוברת לארכיון בסיום" / "חוזרת ביום קבוע") are removed. Selected and unselected colors and rings are the same as in the README.
  3. Category chips: unchanged, except the sound/volume helper line under the chips is **removed** (new mode too).
  4. Weekday picker (weekly only): unchanged.
  5. Date / time inputs: min-height **42**.
  6. Summary line: now **plain text**, 13px, `accent-2-800`, with no pill background.
  7. Sound override card (edit only), notes, and "הוספת הערה": unchanged.
- **Autofocus:** when the page opens (new or edit), focus the name input and place the caret at the end of the existing text. On Android, also show the soft keyboard (`requestFocus` + `showSoftInput`, or `FocusRequester` + `LocalSoftwareKeyboardController` in Compose).
- **Keyboard handling:** the window must resize for the IME (`adjustResize` / `WindowInsets.ime` padding) so content is never hidden behind the keyboard.

## 3. Minimal pass across the rest of the app
- **Screen titles** (תזכורות, קטגוריות, הגדרות, ארכיון): 32 → **26px**.
- **Home hero card:** decorative circles removed. Padding 20, radius 28. Name 32 → 26px, margin 6/0/4.
- **Home "later" rows:** the weekly repeat icon is removed. Everything else is unchanged.
- **Home empty state:** the illustration (circles + bell) is removed. Only the title, body text and the "תזכורת חדשה" button remain.
- **Categories:**
  - Subtitle removed.
  - Rows are plain list rows instead of cards: padding 13×12, radius 22, gap 2 between rows, hover `surface`, press `neutral-300`.
  - The color circle is 14dp (was 40).
  - Trailing content is only the sound name (13px, `neutral-700`). The volume line and the chevron are removed.
  - "קטגוריה חדשה" is a ghost button (plus icon 16dp, `accent-700`, start-aligned, margin-top 6) instead of the dashed box.
- **Archive:**
  - Subtitle removed.
  - Rows are plain list rows instead of cards (padding 10×12, gap 2) with a 10dp dot.
  - "שחזר" is a ghost button instead of a secondary one.
- **Settings:**
  - Leading icons removed from both cards.
  - The snooze title is shortened to "משך נודניק".
- **Bottom navigation:** background = page `bg` (was `surface`); padding 8/12/22.

## 4. Android system bars / edge-to-edge
The app must never draw under the 3-button navigation bar (back/home/recents) or the gesture bar. Apply `WindowInsets.systemBars` / `navigationBars` padding to the root, so the bottom nav, FAB and toast all sit above the system bar. The prototype's fake status bar and home indicator exist only inside the mock frame and are not built.

## 5. App icon & launch screen (Paper)
See `App Icon and Splash.dc.html`. Replaces the old Organic (orange) icon and splash.
- **Adaptive icon:** 108dp canvas. Background layer: solid accent `#4170cb`. Foreground: white bell (same glyph as the "תזכורות" nav tab), 48dp, centered inside the 66dp safe zone, stroke 2.2, round caps/joins.
- **Monochrome layer** (Android 13 themed icons): the bell only.
- **Play Store:** 512×512, same art, no shadow.
- **Launch screen** (SplashScreen API): `windowSplashScreenBackground` `#ffffff`; `windowSplashScreenAnimatedIcon` = icon foreground; `windowSplashScreenIconBackgroundColor` `#4170cb`. No wordmark or branding image. Default fade-out once home is ready.
