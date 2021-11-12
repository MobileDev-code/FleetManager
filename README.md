# Fleet Manager Readme

## Requirements
- Android Studio Artic Fox (latest)
- Minimum Android SDK is 22
- Target Android SDK is 30

## Codebase Architecture

1. Uses RXJava for the Android asynchronous framework
2. Uses MVVM (Model-View-ViewModel) for the standard architecture
3. Uses The Repository Pattern for centralized data access
4. Uses Android Hilt for DI

## Quick start - try it out

1. Build and run the App
2. The app will load the Drivers and present in the main view
3. Drivers will be auto-assigned routes based upon an algorithm when the app starts
4. Clicking on one of the drivers will show the auto-assigned route with the driver

## Assumptions

1. Went with the data model as provided, but it would have been cleaner to have separate data models for the Driver and Shipment with unique ids
2. Y is sometimes considered a vowel.  To keep this exercise simple it considers the standard letters aeiou as vowels


