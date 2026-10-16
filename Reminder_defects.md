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
10. When clicking the triangle next to a sound in the list for selection (in category / reminder), it doesn't play the sound as you would expect.
9. When opening a category for editing, I have to scroll it to see the full editing page
5. When a reminder is fired as a notification, the user should be able to mark it complete within the notification itself.
4. When a reminder is showing as a notification, it should show the name of the alarm and its category.
8. When dismissing a weekly reminder it keeps it as up next, which is wrong, as next time it should fire only next week. 
7. The app allows to set an alarm for a past date/time. Should prevent. To make it wasy, the default time will be the upcoming שעה עגולה
6. The days of week presented for recurring reminder should start from right to left
a. צלצול רציף


## ✔️Manually verified
3. When creating a new reminder, the keyboard is hiding the control so the user can't see what he is typing.
2. The android bottom bar (with the 3 buttons for apps,home,back) is hiding some of the bottom buttons of this app. I want the screen not to span below these buttons.
1. When trying to pick a different sound for a reminder or a category, the app crashes.

## ❌ Failed in retest