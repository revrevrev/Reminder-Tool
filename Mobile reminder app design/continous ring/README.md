# Handoff — Continuous ring (delta)

This package describes **only one change**, made on top of `design_handoff_reminders_app/` + `design_handoff_default_category/`. Everything not mentioned here is unchanged.

## Summary
The alarm can ring **continuously** until the user responds. When it's off, the alarm rings **3 times** and then goes quiet. You set it on the category as a default, and a reminder can override it the same way it overrides sound and volume.

## 1. Category sheet — new row
Placed directly **below** "עוצמת ברירת מחדל" (volume slider), above the note text.

- Row: `.card`, horizontal, radius `radius-lg`, padding 12×16, gap 12. This is the same container as the "צליל ברירת מחדל" row.
- Contents: a 18dp repeat icon, the label "צלצול רציף" (flex 1), and a switch.
- No subtitle.
- Switch: 48×28 track, radius 999, padding 3, 22dp white thumb with `shadow-sm`.
  - On: track `accent`, thumb at the end.
  - Off: track `neutral-400`, thumb at the start.
  - Transition 200ms.
- The value is saved with the rest of the category when the user taps שמירה/יצירה.
- New category default: **off**.
- The "חל על…" note counts a reminder as customised (מותאם אישית) if it overrides **any** of sound, volume or continuous.
- Empty-category note copy: "כל תזכורת בקטגוריה הזו תקבל את הצליל, העוצמה ומצב הצלצול האלה, אלא אם תותאם אישית."

## 2. Categories tab
- The sound label on each row adds " · רציף" when continuous is on (e.g. "פעמון · רציף").

## 3. Reminder sheet (edit mode) — "צליל ועוצמה" card
- **Override off:**
  - The subtitle adds " · רציף" when the category is continuous, e.g. "לפי כספים: פעמון · 80% · רציף".
  - The category hint (catHint) in the category picker adds the same suffix.
- **Override on:** a new row below the volume slider.
  - Contents: a repeat icon, the label "צלצול רציף" (flex 1), and the same switch as in §1.
  - No subtitle.
- Turning the override **on** copies all three values from the category: sound, volume and continuous.
- Turning the override **off** clears all three back to inherit.
- Changing any one of sound, volume or continuous writes all three, so the override is always complete.

## 4. Alarm behaviour
- Each ring plays the sound once. Rings repeat every ~2.6s.
- **Continuous:** keep ringing until נודניק (snooze) or סיימתי (done).
- **Not continuous:** stop after 3 rings. The alarm screen stays open and silent until the user acts.
- Effective value: `reminder.continuous ?? category.continuous`.
- On Android, map continuous to a looping alarm sound. Map not continuous to a bounded number of plays.

## Data model
```
Category { …, continuous: boolean }            // NEW, default false
Reminder { …, continuous: boolean | null }     // NEW, null = inherit from category
```
- A reminder is "customised" if `sound != null || volume != null || continuous != null`.
- Seed values: כספים `continuous: true`, the others false; "להוציא את הפחים" overrides `continuous: true`.

## Files
- `Reminders App.dc.html`: the updated prototype. Open it in a browser with `support.js` alongside. Use it as a design reference only; don't ship it.
