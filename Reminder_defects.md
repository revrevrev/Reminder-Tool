## MANDATORY - Read this first. no exceptions

**STOP. Do NOT read source files or touch any fix code until step 2 is done.**

1. Write the regression test first — exercises the broken behavior (what the user experienced going wrong).
2. Run it — it **must FAIL**. If it passes, the test is wrong; revise it before proceeding.
3. Only now read source files and implement the fix.
4. Run the test again — it must now PASS.

**Why:** A defect means there was no test guarding that behavior. Writing the test first proves it catches the real bug and isn't accidentally passing due to the fix already being in place. Skipping this makes the test worthless — it may pass regardless of whether the bug is fixed.

Note: After fixing a defect, move it under Ready for Testing. Never move it under Manually verified, that's for me to do!
## ##########################################

## Active

## ❔Ready for Testing
15. Too many crashes. Need to analyze
14. When an alarm is fired, I hear two sounds together.
13. When changing the default sound for a category it doesn't affect any existing/new reminder from that category
c. When restoring a reminder from archive its date should be on the upcoming day of week which is the same day of week as the original date.
d. When changing a reminder from oneof to weekly, its day of week should be same as the day of week of the oneof reminder date.


## ✔️Manually verified
7. The app allows to set an alarm for a past date/time. Should prevent and show a clear message
8. When dismissing a weekly reminder it keeps it as up next, which is wrong, as next time it should fire only next week. 
11. The notification should offer also the snooze option.
12. When touching the notification, the sounds stops and it is dismissed immediately.
10. When clicking the triangle next to a sound in the list for selection (in category / reminder), it doesn't play the sound as you would expect.
3. When creating a new reminder, the keyboard is hiding the control so the user can't see what he is typing.
2. The android bottom bar (with the 3 buttons for apps,home,back) is hiding some of the bottom buttons of this app. I want the screen not to span below these buttons.
1. When trying to pick a different sound for a reminder or a category, the app crashes.
9. When opening a category for editing, I have to scroll it to see the full editing page
4. When a reminder is showing as a notification, it should show the name of the alarm and its category.
6. The days of week presented for recurring reminder should start from right to left
5. When a reminder is fired as a notification, the user should be able to mark it complete within the notification itself.
a. צלצול רציף
b. In settings, Add 1 min to the possible snooze options.

## ❌ Failed in retest