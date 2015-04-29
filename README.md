# maze
The Maze

Created the random Maze and room generation algorithm for the game. At the moment for testing purposes, you can run the maze generator though GenerationTest.java which will print out an ascii representation of the maze's paths, rooms, and junctions. The Maze is defined by a 2D grid of Maze Entities which will be used in constructed the actual maze in jMonkeyEngine. Zane did this portion of the project.

We have the structure of our project designed, and have implemented the player Fps control and the physics for collisions. We also have the textures, and bump mapping set up for the walls textures, and the floor. Paul and Edgar contributed the physics and the textures.

Due to change in intended plans for the game design direction certain elements have been removed from what we are now aiming for. The game is strictly a horror driven experience. To add to the dreaded fear we want to instill into the lucky players we decided to move away from the idea of weapons, and are giving the player only a flashlight to "defend" with. (Flashlight, and bumpmapping was done by Andrew) Furthermore, the only random pickups are now batteries. We will have the game be consist of only three different enemy types with their own specific behavior.(We refrain from divulging this information) The goal of the game will stay the same, get out of the maze, get out alive. Because of the nature of the game our focus is primarily on setting the correct fill feel to the game now. Correct lighting from the only light source in the game, the flashlight. And proper events that should be triggered in a given situation as well as a "glorious" audio soundtrack.

Shortcomings for the milestone:
We spent a ted bit to much time trying out different game engines before settling on this one. This ate up way to much time and now we are in constant crunch time. Ultimately we would of loved to have the maze completely rendering at this point, and the flashlight to not have a bug in it.(I believe it has to do with the way jMonkey calculates intensity for spotlight sources, using the position of the controller relative to the spotlights range. We are replacing our code with a spatial/node as a frame of reference for the source instead of the rootNode and setting the light controller, for the flashlight, to the that spatial.) At the moment the maze generation may yield a complete maze 80% of the time. An major concern for us is optimizing and adjusting this percentage. While we are behind schedule at the moment we have a much stronger grasp of what we are aiming for. As far as the allocation of our time from here on out.

Zane - Fully augment the maze generator for an acceptable percentage in generating an complete maze, and render it. Procedurally set event point throughout the maze for enemies, batteries, and other.

Paul - Work on Enemy patterns and behavior.

Andrew - Further improve lighting. The flashlight and lighting is stressed to bring the right feeling to the game. Implement audio events for the atmosphere of the game. 

Edgar - Further tweak player movements, and game states.
