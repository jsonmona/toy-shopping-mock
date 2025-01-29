## Android Shopping App Mockup

### Translation
English (this file)  
한국어 ([README.md](README.md))

### Overview
As a midterm assignment for the Mobile Programming course, I developed a simple shopping app mockup.
All functionalities operate offline and do not require an internet connection.

The app targets API 32 (Android 12L) and runs on devices with a minimum API level of 21 (Android Lollipop).

A debug build APK file is available in the Release section.

### Features
 * Login and registration pages
 * Product listing page
 * User information page
 * Utilizes an embedded SQLite3 database
 * Ability to add products
 * If a user attempts to access account information without logging in, a prompt asks whether they want to proceed to the registration page
 * Duplicate ID check during registration
 * Automatically inserts hyphens when entering a phone number
 * Validates passwords to ensure they are at least 8 characters long and contain a special character (alphabet and number inclusion is not checked)
 * Updates to user information on the profile page are automatically reflected in the database
 * Pressing the back button while logged in immediately exits the app, but when not logged in, it redirects to the login page

### Notes
The dialog prompting users to register will display "CANCEL" and "OK" when viewed in an emulator.
This is because the system language is set to English. Changing the system language will display the text in Korean.

Passwords are stored using SHA-256 hashing.
Avoid storing real password since salt is not used and might be vulnerable to potential security risk.
