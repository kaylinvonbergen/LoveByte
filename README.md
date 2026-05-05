# LoveByte 
#### Learn to love — and to program!

## What is LoveByte?

LoveByte is a mobile, narrative-driven educational game that teaches basic programming concepts through microlearning and anthropomorphism. The game blends elements of traditional visual novels and dating simulators (loveable characters, branching dialogues, minigames) with interactive coding challenges, transforming abstract and intimidating programming languages into character-driven experiences. 

Ultimately, LoveByte addresses a well-documented problem in computer science education: many beginners, especially young women, perceive programming as intimidating, overly technical, and inaccessible. This contributes to high early-stage attrition and frustration. LoveByte operates differently, framing programming as something personal, engaging, and emotionally resonant. 

LoveByte personifies programming languages with distinct personalities, dialogue, and narrative arcs. Users progress through short, self-contained chapters focusing on a single programming concept, containing interactive dialogue with their language of choice and potentially minigames. The chapters are designed to be completed in around 5 minutes, but users can also choose to combine multiple chapters in a single session.

---

## Architecture 

LoveByte follows a layered architecture based on the Model–View–ViewModel (MVVM) pattern, separating UI rendering, state management, narrative content, and data persistence. This structure allows the application to remain modular, scalable, and easy to reason about as features expand. 

### Navigation and Application Entry 

The application entry point is MainActivity, which initializes the shared ViewModel, manages runtime permissions (location and activity recognition), and defines all navigation routes using Jetpack Compose Navigation. The app transitions between screens such as splash, home, character selection, timeline, and chapter gameplay through a NavController, allowing a clear and consistent flow between experiences. 

User interactions (such as selecting a chapter or making a dialogue choice) are passed upward through callbacks and handled centrally, rather than directly inside UI components. This ensures that navigation logic and state changes remain decoupled from presentation.

### ViewModel and State Management 

At the core of the architecture is LoveByteViewModel, which acts as the single source of truth for the entire application. All UI is driven by a unified state object (LoveByteState), which contains information such as current language, chapter progression, dialogue position, onboarding state, minigame activity, sentiment scores, and external data like weather and location. 

State flows downward from the ViewModel into Compose screens, while user actions flow upward through event callbacks. This unidirectional data flow simplifies reasoning about the app’s behavior and ensures consistent UI updates across all screens.

### Narrative System Design 

LoveByte uses a fully data-driven narrative system. Dialogue is represented as a network of DialogueNode objects, each containing text, speaker information, emotional state, branching choices, and optional trigger events. 

Rather than hardcoding story logic into UI components, all narrative flow is defined in structured data. This allows for flexible branching paths, easy iteration on dialogue, and scalable content expansion.

User choices affect three sentiment dimensions: love, friend, and hate. These values accumulate over time and influence the final outcome of the narrative. Importantly, different endings represent different relationship paths rather than success or failure states.

### Minigame Integration 

Minigames are integrated into the narrative through event triggers embedded within dialogue nodes. When a node contains a trigger event, the UI transitions into the corresponding minigame experience. 

Each minigame returns a success or failure outcome, which is then used by the ViewModel to determine the next dialogue node. This event-driven design keeps gameplay systems loosely coupled from narrative logic, improving modularity and maintainability.

### Separation of Concerns 

LoveByte enforces a clear separation between layers:

UI Layer (Compose Screens): Responsible only for rendering and user interaction  
ViewModel Layer: Handles state updates, progression logic, and orchestration  
Data Layer: Manages persistence and external data sources  

This separation improves code clarity, supports scalability, and aligns with modern Android development best practices.

---

## Database Usage and Schema

LoveByte uses a local SQLite database through the Room persistence library to store user progress across sessions. This allows users to close the application and later return to the exact point they left off, including dialogue position and relationship state.

### Database Structure 

The core entity in the database is UserProgress, which stores progress on a per-language basis. Each record represents the user’s current state within a specific programming language storyline.

The schema includes:

- language (Primary Key): identifies the programming language (e.g., Python, Kotlin)  
- chapterId: the current chapter the user is on  
- dialogueIndex: the current position within the chapter  
- lovePoints: accumulated sentiment toward the “love” path  
- friendPoints: accumulated sentiment toward the “friend” path  
- hatePoints: accumulated sentiment toward the “hate” path  

This structure allows LoveByte to reconstruct both narrative progression and relationship outcomes.

### Data Access and Repository Layer 

Database access is handled through a Data Access Object (UserProgressDao), which provides both reactive and one-time queries. A Flow-based query is used when the UI needs to automatically update in response to changes, while suspend functions are used for direct reads during initialization or logic execution.

A repository layer (ProgressRepository) sits between the ViewModel and the DAO. This abstraction prevents the ViewModel from directly interacting with database logic, improving separation of concerns and enabling easier testing and future extensibility.

### Persistence Design Considerations 

The database is intentionally minimal, storing only essential progress data rather than full narrative state. Narrative content itself is stored separately in static data files, allowing the database to remain lightweight and efficient. 

By separating narrative data from user progress, LoveByte ensures that content updates can be made without affecting stored user data.

---

## API Integration

LoveByte integrates an external weather API to provide contextual environmental data within the application. This data is used to enhance immersion by incorporating real-world conditions into the game environment.

### Network Layer Design 

API communication is handled through Retrofit, a type-safe HTTP client for Android. The network layer is structured with:

- WeatherApi: defines the available API endpoints  
- RetrofitProvider: configures the HTTP client and base URL  
- WeatherRepository: acts as the intermediary between the API and the ViewModel  

This layered approach ensures that network logic remains separate from both UI and state management.

### Data Flow 

When the application requests weather data:

1. The ViewModel calls the WeatherRepository  
2. The repository makes a network request through Retrofit  
3. The API response is parsed into structured data  
4. The ViewModel updates LoveByteState with the new weather information  
5. The UI automatically recomposes to reflect updated conditions  

### Location Integration 

The weather system is paired with device location data, retrieved through Android’s location services. Location permissions are requested at runtime, and the app gracefully handles cases where permission is denied.

This combination allows LoveByte to display location-specific weather conditions such as city name, temperature, and weather description.

### Design Considerations 

The API integration is designed to be lightweight and non-critical to gameplay. If the network request fails or permissions are denied, the application continues to function normally without blocking the user experience.

This ensures robustness across varying network conditions and device configurations.

---

## Sensor Integration 

Lovebyte incorporates multiple onboard device sensors to solidify learning by utilizing interactive, physical learning experiences. Each integration is purposefully mapped to specific educational objectives, reinforcing concepts through embodied interaction. 

All sensors are accessed through the Android SensorManager, with updates handled via the SensorEventListener. This allows the application to register and unregister listeners dynamically, ensuring that sensor data is only collected during active gameplay. This follows Android best practices to reduce battery usage and avoid unnecessary background processing. 

### Gyroscope (Code Structure & Indentation)

The gyroscope sensor is used in the Syntax Slider minigame to reinforce code structure and indentation through device rotation. 

Though the gyroscope is capable of measuring angular velocity across three axes, the minigame uses only the Y-axis tilt mapped to horizontal movement. Users tilt their device to slide lines of code horizontally into the correct indentation level. The movement depends on how significantly the user tilts their phone, and locks the line into place once correct alignment is reached.

This interaction mirrors how indentation defines structure in languages like Python. Instead of having to memorize indentation rules, users can develop an understanding by physically aligning the blocks of code themselves. 

In order to improve the user experience, we added a couple features. A “neutral zone” prevents accidental overshooting by halting motion after one code block has been “locked” until the phone reaches a neutral position. Code block “snapping” into place provides feedback when alignment is correct. 

For accessibility and stability, there is a drag-based “Public Mode” to allow users to utilize the game even when mobility is limited. 

### Step Counter / Pedometer (Algorithmic Efficiency)

The step detector / step counter sensor is used in the Efficiency Stepper minigame to represent loop execution and algorithmic complexity.

These are motion-based sensors that detect physical movement events. The step counter provides cumulative step data, while the step detector triggers discrete step events in real time. Both are used to map physical movement to iteration counts in code execution. 

In this minigame, users physically take a number of steps corresponding to the number of iterations required by a given piece of code. By tying movement to iteration count, the game converts an abstract concept (Big-O intuition) into a tangible experience. Users learn both how loops function in Python and begin to understand what the cost of inefficient logic could look like. 

To ensure usability, the app supports both TYPE_STEP_DETECTOR and TYPE_STEP_COUNTER sensors for broader device compatibility. Sensor availability is checked at runtime using getDefaultSensor(), ensuring graceful degradation on unsupported devices. 

Sensor listeners are only registered during active gameplay and unregistered immediately afterward to reduce battery usage and prevent unnecessary system load.

A Public Mode fallback replaces physical movement with a slider-based interaction for accessibility and low-mobility scenarios, ensuring that sensor-based learning is optional rather than required


### Ambient Light Sensor (Security & Privacy Concepts)

The ambient light sensor (oftentimes the front camera) is utilized in the Light Sensor Minigame to reinforce themes of data privacy and security. The sensor is an environmental sensor that measures illumination in lux units. It provides real-time data about the surrounding physical environment, allowing applications to respond to lighting conditions. 

Users must physically cover their device’s light sensor to reduce detected light levels below a certain threshold. It metaphorically represents “hiding” or “encrypting data” – in this case, hiding secrets like API keys in a .env file.

To improve user experience, we added a short calibration delay to prevent accidental completion, a visible progress indicator tied to real-time lux values, and a threshold-based success condition. 

To maintain accessibility, the “Public Mode” option allows users to complete the task via a simple UI interaction: tapping a button. A timed bypass option appears to prevent user frustration or allow the user to “fail” the game if they want to pursue a negative relationship with the character. 

### Sensor Framework Design Considerations

Across all implementations, LoveByte follows Android’s sensor framework architecture and best practices:

- Sensors are accessed through the SensorManager system service  
- Sensor availability is verified at runtime using getDefaultSensor()  
- Sensor data is received through SensorEventListener callbacks (onSensorChanged, onAccuracyChanged)  
- Sensor listeners are registered only while the app is in the foreground and actively using the feature  
- Listeners are unregistered immediately using sensorManager.unregisterListener() to conserve battery  

---

## UI/UX & Accessibility

### UI Driven By State (Unidirectional Data Flow)

The application UI is fully driven by a single state holder (LoveByteState) exposed from LoveByteViewModel. This follows the Single Source of Truth design pattern, where all UI screens render based on immutable state updates. State flows down, while events, like onChoiceSelected() and advanceToNode(), flow up.

### Screen Architecture and Separation of Concerns

LoveByte follows a layered architecture:

#### UI Layer (Compose Screen)
Includes GameScreen, CharSelectScreen, HomeScreen, SettingsScreen, TimelineScreen
Responsible only for rendering UI and sending user events upward
No direct business logic or data persistence 

#### ViewModel Layer
LoveByteViewModel 
Handles dialogue progression, onboarding flow, minigame routing, etc.

#### Data Layer
Handles databases and network operations 
ProgressRepository, WeatherRepository, etc. 

The separation we used improves cohesion, low coupling, and testability.

### Compose State Management and Lifecycle Safety

Our app has significant important data that must survive configuration changes, so things like dialogue progression, minigame state, onboarding flow, and weather/location data exist in the ViewModel (LoveByteViewModel) to ensure persistent app state.

### Lazy Loading and Performance in UI

LoveByte uses Compose to ensure best practices in terms of efficiency. HorizontalPager in the CharSelectScreen allows for efficient paging instead of full list rendering. Image loading is done with Content.Scale.Fit to prevent layout overflow. LazyColumn is used in the TimelineScreen to ensure only chapters visible on the screen are reloaded to improve performance.

### User Experience Design

#### Narrative-first UX 

This app prioritizes immersion through full-screen dialogue experiences, character sprite anchoring, and minimal UI distraction during storytelling. This reduces cognitive load and assists in keeping the focus on narrative flow and educational content.

#### Progressive Disclosure

Information is revealed gradually:
onboarding → language selection → chapter → dialogue → minigame

This prevents overwhelming new users and aligns with LoveByte’s game-like learning structure. 

#### Feedback and State Visibility

The UI consistently reflects system state. Chapter completion dialogue confirms performance, sentiment bars (LOVE/FRIEND/HATE) visualize relationship state, and progress percentage shows completion per language.

This improves user motivation, clarity of progress, and the engagement loop reinforcement LoveByte utilizes to further learning. 

### Accessibility Considerations 

Though LoveByte mimics dating sim pixel aesthetics, accessibility is maintained through Compose’s semantic system and design choices. 

#### Touch Target Accessibility 

Buttons, such as PixelButton and IconButton, are spaced using padding and weight layout to ensure UI use for people with limited dexterity. Minimum tap area compliance is also utilized. 

#### Content Readability 

Dialogue text uses higher line spacing to ensure readability.

Color contrast is maintained, with “darkMatcha” and “deepPink” being used for only large text, “inkBrown” for small/normal texts, and other colors for non-essential graphical elements. LoveByte also avoids relying on color alone for meaning, instead utilizing different fonts, font sizes, and icons to convey information to the user. 

#### Design Tradeoffs

LoveByte recognizes that the app’s stylistic similarity to nostalgic dating sims results in a slight reduction in readability compared to standard Material themes, but we have mitigated this with strong color contrast, large font sizes, and minimal text density per screen. 

---

## Testing

LoveByte was tested using a combination of ComposeUI tests, unit tests, and logic-level verification to ensure correctness across UI behavior, state management, navigation, persistence logic, and core game systems. Testing was designed with the app’s MVVM architecture in mind, with clear separation between UI-layer validation and pure logic validation.

### Compose UI Testing (User Flow Validation)

Most testing focused on Jetpack Compose UI tests using createComposeRule(). These tests validate that the UI behaves correctly from the user’s perspective by simulating interactions such as clicks, text selection, and navigation events.

* Character Selection Screen
    * Verifies correct sprite rendering per language
    * Tests pager navigation (next/previous character switching)
    * Ensures selection callbacks are triggered correctly
    * Validates info dialog behavior and fallback UI states
    * Checks edge cases such as missing or incomplete state data

* Game Screen (Narrative System)
    * Ensures dialogue nodes render correctly (speaker, text, emotion, sprite)
    * Tests tap-to-advance flow when no choices are present
    * Validates branching logic when dialogue choices exist
    * Ensures callbacks fire correctly for node progression and chapter completion
    * Verifies minigame triggering behavior via triggerEvent
    * Confirms safe handling of null or missing nodes

* Home Screen (Onboarding & Dashboard Flow)
    * Tests multi-step onboarding flow (steps 1–4)
    * Validates proficiency selection and placement logic UI
    * Ensures dashboard reflects correct state (start vs continue)
    * Verifies button callbacks for navigation and settings access
    * Confirms UI adapts to different LoveByteState configurations

* Settings Screen
    * Validates correct rendering of settings UI sections
    * Tests private mode toggle state synchronization
    * Ensures user interactions trigger correct callbacks
    * Verifies accessibility-related informational text is displayed
    * Confirms button interactions (replay onboarding, change proficiency)

* Timeline Screen
    * Tests chapter progression UI for multiple languages (Python, Kotlin)
    * Ensures scrollable LazyColumn content behaves correctly
    * Validates chapter unlock/lock logic through click testing
    * Confirms progress percentage calculation is displayed correctly
    * Verifies ChapterCard UI states (completed, current, locked)

### Unit Testing (Core Game Logic)

In addition to UI testing, LoveByte includes pure JVM unit tests that validate core logic independently of the Android framework. These tests focus on deterministic systems such as:

* Sentiment System
    * Verifies correct updates to love, friend, and hate scores
    * Ensures values are properly clamped between bounds (0–50)
    * Confirms dialogue choices correctly modify sentiment state

* Narrative Logic
    * Validates dialogue choice routing to correct node IDs
    * Ensures chapter start node mapping is consistent
    * Verifies minigame success/failure routing logic

* Utility Logic
    * Tests weather-to-adjective mapping for API integration
    * Ensures helper functions behave consistently across inputs

These tests isolate business logic from UI concerns, ensuring correctness even without Compose or Android runtime dependencies.


## Multi-Device Testing
We used a physical Samsung S25 Ultra as well as the emulator in Android Studio to test. This was important for seeing visual issues and demonstrating accurate to life usage scenarios.

### Debugging
Debugging was done using breakpoints, state inspection, tracing error stacks, and, as a last resort, using the help of generative AI like Gemini. 

---

## Team Contributions 
Kaylin worked on core application systems including data persistence, API integration, and overall state management. This included implementing Room for storing user progress, integrating a weather API with location data, and structuring the app’s ViewModel-driven architecture to support reactive UI updates and narrative progression.

Anna worked primarily on the UI layer, gameplay systems, sensor integration, and testing infrastructure. This included implementing core Jetpack Compose screens such as GameScreen, HomeScreen, TimelineScreen, and supporting navigation-driven flows across the application. Additionally, Anna was responsible for managing sensor functionality, including lifecycle management and mapping sensor input to meaningful gameplay mechanics. Additionally, Anna created all character sprites in LoveByte, drawing the Python character and all his variations. 

---

## AI Reflection

AI was used in the early stages of development to brainstorm and refine LoveByte’s concept, assisted with scope feasibility, suggested alternative forms of learning progression, and refined the “elevator pitch”. It also helped to split work between partners. Each team member used AI slightly differently for their own portions:

### Kaylin

Kaylin used AI tools throughout the semester to assist with debugging, identifying errors, generating basic template code, supporting narrative brainstorming, and explaining complex or confusing concepts encountered during development.

AI influenced multiple areas of the project, including architecture, implementation, testing, and UX decisions. It was particularly useful for suggesting initial structural approaches (such as MVVM organization), generating boilerplate or template code, and helping reason through unfamiliar APIs, sensor behavior, and state management patterns. AI also helped accelerate development by reducing time spent on syntax issues, debugging small errors, and quickly prototyping components that could then be refined.

However, AI was less effective when making narrative design decisions or implementing features that required coordination across multiple files and systems. In these cases, suggestions often lacked awareness of the broader codebase or the intended user experience. As a result, many AI-generated ideas—particularly around narrative flow and cross-component logic—were modified heavily or rejected entirely in favor of manually designed solutions.

All AI-generated suggestions were carefully reviewed, tested, and iterated upon before being incorporated into the final codebase. The final implementation reflects our own design decisions, with AI serving as a supportive tool rather than a source of truth.


### Anna 

Anna used Google Gemini as a support tool across ideation, implementation, testing, and documentation, though it was treated as an assistive system (or pair programmer!) rather than an authoritative source of truth. All outputs were reviewed manually and validated for accuracy and best practices.

Some AI-generated suggestions were rejected when they conflicted with MVVM boundaries or introduced unnecessary coupling between UI and logic. In particular, proposals that embedded business logic directly in composables were refactored into ViewModel or repository layers.

Gemini was also used to assist with making a more pixel-inspired UI while staying within Material guidelines, as well as generally suggesting potential UI improvements like clearer progression indicators through chapters and potential “public mode” alternatives for games. 
 
Gemini generated the pixelated splash screen in large part to add joy and whimsy (Anna did not know how to do the spawning math).

Gemini was used to generate the initial test structure for Compose UI tests and unit tests, which were then manually refined to match actual architecture.

Gemini was also used to generate early-stage placeholder narrative content and assist character concept art and personality.

All design decisions, architecture choices, and final implementations were made manually, with AI serving as a supplemental tool for clarification, acceleration, and exploration of alternatives rather than as an authoritative code generator– Anna ensured to examine suggestions critically, and even when Gemini suggested “new” things we didn’t know about, took the time to look into them and understanding them (E.g. HorizontalPager).

---

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



