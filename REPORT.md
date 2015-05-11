# Introduction

The Maze is a first-person. horror game inspired by the likes of Slender and Amnesia. The player is equipped with nothing more than a flashlight to illuminate the entire maze in front of him. Monsters will chase him as he descends into the infinite labyrinth. Only with luck, skill, and cunning will he be able to escape.

[Gameplay video](fill in link here)

## Conception

Our initial game idea was to implement something similar to the movie Maze Runner. During the day, one could explore the beautiful maze with its exquisite scenery, and during the night, one would be pressured to find the way out, or shamefully crawl their way home. The idea was intriguing and we enjoyed it, yet we ran into issues when implementing it.

The first issues was that of aesthetics. During the day, since a global illumination would be applied (the sun), one could easily see all textures. This was problematic since it was difficult to find and efficiently render a beautiful scene. Of course it could be plausible with an artist and more time, but given the resource and time constraints, it was impossible. Furthermore, our team had recently started playing horror games in our attempts to procrastinate studying for finals. As such, we admired the works of Slender and Amnesia, and wanted to something similar. The result was The Maze.

The Maze has a simple concept: escape it. The player will be spawned in a randomly-generated maze and will attempt to find the ending room while avoiding enemies and the like. There is no way to fight against these enemies, your only way out is to run. We came up with this idea given the maze generation we were already working on for our initial concept. We played around with some textures and thought it was surprisingly suitable, and went with it.

## Technologies

The game is written in Java and the JMonkeyEngine. We chose to use JMonkeyEngine (JME for short) in that it was more available cross-platform, so we could develop on our Macs, which we had trouble using Ogre for, and since JME provided better lighting capabilities (such as the spotlight).

In terms of the class hierarchy, most of our code is contained inside of the maze package. The only exception is GenerationTest.java, which is used to test the maze generation as a command line interface. The main function is located in Main.java, which simply creates an application from Maze.java and runs it. Maze.java contains the game loop, update functions, and initialization. It generates the Maze, from MazeGenerator.java, which is in the form of a MazeLayout object, creates the player (Player.java) and the mobs (Golem.java), and renders the Entities (either Wall, Door, or Open). And all Entities inherit from AbstractEntity. As such, our code is quite modular and was easy to work on independently.

There are broadly four categories that constituted of the main parts of the project.

* Maze Generation
* Graphics and Physics
* Artificial Intelligence
* Sounds

### Maze Generation

Maze generation is done with our own algorithm that relied on the works of others. The maze is completely randomly generated and relies on the inputs to the MazeGenerator constructor, such as minimum rooms, max rooms, windiness, and so forth. The gist of the algorithm is to generate the rooms first, using a simple heuristic (placing the rooms and seeing if the room overlaps with any other). Both the position and the dimension are random. Then, we attempt to draw connecting paths between the rooms. This is done with the Growing Tree algorithm, which essentially has a random chance between "winding", turning in a random direction, or moving forward. This guarantees that the maze is solvable. Finally, we return an array of Enums wrapped as MazeLayout, and generate the maze (render the objects) in Maze.java. Zane was responsible for most of the maze generation and Paul was responsible for rendering the maze and writing all of the Entities.

### Graphics and Physics

Graphics and physics are done through OpenGL and Bullet, which is wrapped for us by the Bullet physics engine. The maze itself is composed of Entities, as mentioned before. Each Maze spot can either be open, a wall, or a door. An open space is simply a box underneath the player and another box above the player. A wall is similar with an orientation. The door is similar as well but with a different texture (along with interactivity). Paul wrote most of the code for the Entities.

The next big component was the user model and the enemy model. The user model is implemented by attaching a camera to the user physics object (which is a PhysicsCharacter modeled as a sphere). Enemy models are generated from the mesh and moved with a localTranslation function. Paul and Andrew wrote most of the code here.

Finally, the only source of lighting in the game is from the user's flashlight, which we ran into quite a few issues with. The main issue was that we were running into weird flashing particles of all colors at the rims of the flashlight. This was odd in that we never touched any color outside of white, grey, and orange. The reason was that an if-branch statement relied on branch prediction of the GPU, which wasn't feasible for on-board graphics computation, such as the ones found on our Macbooks. We asked the JME forums and received the reply from a [core developer](http://hub.jmonkeyengine.org/t/issues-with-spotlight-and-normal-maps/32466/3). Therefore, the game runs fine on the lab machines, but results in the weird effect for the Macbooks. Paul and Andrew wrote most of the code for the lighting as well.

![Weird effect](http://i.imgur.com/5kAntzq.jpg)

### Artificial Intelligence

We currently have one mob (have plans to implement one more till Wednesday) which we dub the Golem. The Golem does not have any animations, and instead stays still when the player is looking at it. The Golem does update its position when it detects that the player is not looking at it. Therefore, when the player is busy trying to navigate the maze, and suddenly turns around, the Golem would suddenly appear. This proved to be quite effective, scaring the developers during play-testing and our friends as well.

The models for the mob are Otto, which is a black suit of armor from the default JMonkeyEngine test data. It would've been preferable to have our own model with Blender and the like, but none of us knew how. The AI is done as described above, but our main challenge was ensuring that only the Golem in the Room was running the computation for the viewport, which is rather computation-heavy. Not all of the golems can do so. Therefore, we had to check if the player was within a room for each golem and then run the update code only if that check was true. Paul was responsible for the AI.

### Sounds

Finally, sounds are what is truly important for a horror game and we intended to deliver. There is a background check which is a standard ambient track for a horror game, low, steady percussion. However, this background check involves quite a bit of noise. Noise in the sense of randomly jarring instruments. This gives the sense of a horror impending when there not necessarily is one.

The real sound, however, was done through some clever growls from our voice actor Zane. This sound is similar to the noise in the background track, but represents the distance between the Golem and the player. Therefore, the player will be listening for this increasing sound (based on distance), but will also be disoriented by the myriad of other noises.

Finally, we have auxiliary sounds for running and opening doors.

### Team

* Paul: Graphics & Physics, Artificial Intelligence
* Zane: Maze Generation, Sound
* Andrew: Graphics & Physics
* Edgar: Sound & HUD
