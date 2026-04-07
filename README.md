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


## Major TODOs for next time 
- [ ] "cute-ify" the UI, it's currently functional, but not pretty
- [ ] Polish UX, quality of life updates to navigation
- [ ] Work on game content
- [ ] Develop further minigames
- [ ] Make it possible to fail minigames
- [ ] Establish sentiment/feelings systems and Love/Friend/Hate routes
- [ ] Potentially implement audio?
- [ ] Implement settings screen
- [ ] Work on UI layout for horizontal modes and tablets

## AI Disclosures

### Anna's portion 
During the brainstorming and planning phase, AI (more specifically, Gemini) was utilized to help establish which parts of our project needed to be settled prior to asynchronous, independent work. It was helpful in establishing some of these guidelines and reminding us about some details (e.g., screens that would be developed later should still have little stub components in order to ensure the app as a whole complied if we wanted to establish navigation prior to independent work). It also helped us figure out certain naming schemes/conventions so we could navigate our files and functions intuitively.

In terms of data models, Gemini assisted in verifying where various data should be, such as separating models for a particular minigame from general game state models or models used to keep track of narrative progression. This was helpful as it helped give the organization a more logical flow.

In terms of lifecycle management for sensors, Gemini assisted with implementing a DisposableEffect with an onDispose block to help unregister the SensorEventListener. Because the minigame (and thus the gyroscope) pops up in the middle of the chapter, figuring out how to properly “dispose” of it part way through the chapter was very important to maintain battery and performance. 

Gemini generally assisted in finding the Material3/Android/Kotlin/Compose names for things already encountered in webdev, like a “carousel” for character selection (HorizontalPager). This was especially helpful, because it allowed for more rapid development rather than digging through documentation to find what elements are called in Android development. 

Gemini was also used to develop placeholder content (info about characters/languages, basic stories, etc.) as good writing is not necessary to develop the functionality of the app. Also silly and honestly decent, these will be replaced down the line when an actual narrative is developed.
