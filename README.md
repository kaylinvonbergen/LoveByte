# LoveByte Checkpoint 4/28/26

## Newest Accomplishments this Update
- **"Cuter" UI**
    - Adjusted colorscheme to be more in-line with traditional dating sims
    - Included use of pixel font in buttons and headers
    - Added bevels/steps to assets to give a more pixelated look
- **Landscape Adaptability/Responsiveness**
    - Main screens (Home, Character Select, Timeline) all support landscape mode
    - Plans to lock orientation for game modes
- **Character Sentiment** 
    - Dialogues contribute to "Friend", "Love", or "Hate" routes
    - Will eventually lead to associated endings
- **Further Minigame Development**
    - Pedometer game created
    - Light sensor game created
    - Each game has a "public" (playable in situations where dynamic movement is difficult) mode
    - Each game is now failable

## 📁 Project Structure
```text
app/
└── src/main/java/com/example/lovebyte/
    ├── data/
    │   ├── content/
    │   │   └── NarrativeData.kt
    │   ├── local/
    │   │   ├── AppDatabase.kt
    │   │   ├── DatabaseProvider.kt
    │   │   ├── UserProgress.kt
    │   │   └── UserProgressDao.kt
    │   ├── location/
    │   │   └── location utilities
    │   └── model/
    │       ├── NarrativeModels.kt
    │       ├── GameState.kt
    │       ├── EfficiencyMinigameModels.kt
    │       └── SyntaxMinigameModels.kt
    ├── network/
    │   └── API + networking layer
    ├── repository/
    │   ├── GameRepository.kt
    │   ├── ProgressRepository.kt
    │   └── WeatherRepository.kt
    ├── ui/
    │   ├── components/
    │   │   └── reusable UI + minigames
    │   ├── screens/
    │   │   ├── HomeScreen.kt
    │   │   ├── GameScreen.kt
    │   │   ├── CharSelectScreen.kt
    │   │   └── TimelineScreen.kt
    │   └── theme/
    │       ├── Color.kt
    │       ├── Theme.kt
    │       └── Type.kt
    └── viewmodel/
        └── LoveByteViewModel.kt
```

### 🧠 Architecture Overview

- **data/** → Models, local database, and narrative content  
- **repository/** → Handles data flow between UI and data sources  
- **network/** → External API calls  
- **ui/** → All Compose UI screens, components, and theming  
- **viewmodel/** → State management and business logic  

### 🏗️ Design Pattern

This project follows an **MVVM architecture**:

- **View** → Compose screens in `ui/screens`
- **ViewModel** → `LoveByteViewModel`
- **Model/Data** → `data/` and `repository/`
 
## Testing 
We currently have a dual-layer testing strategy, with unit testing (with Mockito Kotlin!) for business logic and Compose UI tests for the UI/UX. 

### Local Unit Testing
We utilized unit tests to validate the "brain" of the application in isolation. 

* **Stateless Logic:** To improve testability, we refactored some of our data transformations (like Weather API mapping and Sentiment calculation) into **Companion Objects**. This decoupled our business logic from the Android Framework, allowing tests to run without an emulator! Trying to test without Companion Objects proved incredibility complicated, and decoupling was best practice, anyway. 
* **Key Test Cases:**
    * `mapWeatherToAdjective`: Verifies that raw strings from the OpenWeather API (e.g., "Thunderstorm") are correctly translated into user-friendly adjectives ("stormy").
    * `sentimentClamping`: Ensures that character affinity scores stay strictly within the **0–50 range**, preventing UI overflows or negative progress values.
 
### 2. Instrumentation Testing (Compose UI)
We used the **Android Compose Test Library** to verify the integrity of the user interface and user flows. We've started with CharSelectScreen, but we will expand testing over the next week. 
* **State Synchronization:** Automated tests verify that the `HorizontalPager` in the Character Selection screen correctly updates the `currentLanguage` state.
* **User Flow Validation:** We tested the transition between narrative chapters to ensure the `dialogueIndex` remains consistent across configuration changes and session restores.
 
### Testing Tech Stack

| Tool | Purpose |
| :--- | :--- |
| **JUnit 4** | Primary framework for local unit tests. |
| **Mockito** | Used for mocking `Application` and `SharedPreferences` dependencies. |
| **Compose UI Test** | Used for interacting with the Semantics Tree in Jetpack Compose. |
| **Android Studio Debugger** | Utilized with breakpoints to trace state transitions in the `LoveByteViewModel`. | 

## TODOs for next time 
- [ ] Polish sentiment
- [ ] Create endings for each "route" for Python
- [ ] Perhaps create a settings screen

## Stretch Goals 
- [ ] Custom sprites for Python
- [ ] Kotlin storyline and games
- [ ] Game Audio

## AI Disclosure, Updated

### Anna's portion 
Google Gemini was used to assist in "cute-ifying" the app's UI, providing advice on how to acheive a more pixelated look without departing from modern design standards (beveled edges instead of manually creating pixels, etc.). Additionally, it was used to figure out what preliminary testing should look like, as well as resolving issues involving mocking data for unit tests. Most notably, when the AI's initial testing suggestions conflicted with the app's singleton database structure, I manually refactored core logic into Companion Objects to ensure the code was testable in a local JVM environment. Additionally, it helped format this README :] 

### Kaylin's portion 
AI tools (ChatGPT) were used tor refine features like sentiment tracking, onboarding flow, and UI improvements, as well as find likely sources of bugs and think through edge cases in state management (like preventing progress from going backward and handling navigation correctly). I also used AI for guidance on structuring Jetpack Compose components and for generating small example snippets to clarify implementation details. All core logic, design decisions, and final code were integrated and understood by me, and any AI suggestions were reviewed and adapted to fit the project’s specific goals and structure.


# LoveByte Checkpoint 4/7/26

## WHAT WE CAN SHOW OFF
- Home Screen
- Character Selection Screen
- Chapter selection screen
- **PYTHON** full **CHAPTER 1**
- **PYTHON** **CHAPTER 1** minigame

## Accomplishments for This Checkpoint
- core navigation and state architecture complete
- 1 completed minigame with gyroscope integration
- narrative & minigame main loop completed


## TODOs for next time 
- [x] "Cute-ify" the UI, it's currently functional, but not pretty
- [x] Polish UX, quality of life updates to navigation
- [ ] Work on game content
- [x] Develop further minigames
- [x] Make it possible to fail minigames
- [ ] Establish sentiment/feelings systems and Love/Friend/Hate routes
- [ ] Potentially implement audio?
- [ ] Implement settings screen
- [x] Work on UI layout for horizontal modes and tablets
- [ ] Adjust sensitivity for minigame with real device 

## AI Disclosures

### Anna's portion 
During the brainstorming and planning phase, AI (more specifically, Gemini) was utilized to help establish which parts of our project needed to be settled prior to asynchronous, independent work. It was helpful in establishing some of these guidelines and reminding us about some details (e.g., screens that would be developed later should still have little stub components in order to ensure the app as a whole complied if we wanted to establish navigation prior to independent work). It also helped us figure out certain naming schemes/conventions so we could navigate our files and functions intuitively.

In terms of data models, Gemini assisted in verifying where various data should be, such as separating models for a particular minigame from general game state models or models used to keep track of narrative progression. This was helpful as it helped give the organization a more logical flow.

In terms of lifecycle management for sensors, Gemini assisted with implementing a DisposableEffect with an onDispose block to help unregister the SensorEventListener. Because the minigame (and thus the gyroscope) pops up in the middle of the chapter, figuring out how to properly “dispose” of it part way through the chapter was very important to maintain battery and performance. 

Gemini generally assisted in finding the Material3/Android/Kotlin/Compose names for things already encountered in webdev, like a “carousel” for character selection (HorizontalPager). This was especially helpful, because it allowed for more rapid development rather than digging through documentation to find what elements are called in Android development. 

Gemini was also used to develop placeholder content (info about characters/languages, basic stories, etc.) as good writing is not necessary to develop the functionality of the app. Also silly and honestly decent, these will be replaced down the line when an actual narrative is developed.

AI was also used to quickly polish up and format areas of code that looked messy (due to weird spacing, for example) before committing.

### Kaylin's Portion
ChatGPT was used throughout development as a debugging and implementation assistant, especially when integrating external systems like location services and the weather API. It helped identify issues such as incorrect API key handling, improper Gradle configuration, and missing function calls that prevented expected behavior. AI was also used to reason through state management bugs, particularly around chapter completion, progression tracking, and navigation edge cases.

Additionally, due to a lack of prior hands-on experience with multi-screen navigation and local database integration in Android, AI was used to build foundational understanding of these systems. This included learning how screens communicate through a shared ViewModel, how navigation flows between them, and how user progress can be stored and retrieved using a local database. AI helped break down these concepts into practical, incremental implementation steps.

AI also assisted with structuring narrative data so it could be directly imported and used by the game screen without unnecessary abstraction, as well as refining how that data connects to the ViewModel. This was useful for translating high-level ideas (e.g., “this should just be content, not a class”) into concrete Kotlin implementations.

AI was most useful for accelerating debugging, clarifying unfamiliar Android-specific patterns (such as BuildConfig usage and permission flows), and reducing time spent on trial-and-error when integrating multiple systems. It was less useful for nuanced design decisions, where manual iteration and testing were required to achieve the intended behavior and user experience.



