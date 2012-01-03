Puyo-Puyo
Written By: Joshua Speight

Super simple Tetris-like game I implemented in java and later extracted a game engine out of. Here's the source to the game and the engine.
Though there isn't any documentation yet, the commentary is somewhat thorough, and the game files extend the proper classes and demonstrate
how to properly override methods in the engine's classes. Currently the engine is still in its infancy and lacks a lot, but still provides
a level of enough abstraction that much of boilerplate code can be skipped, and the game-specific logic can be focused on without editing any
of the engine's files themselves. By default the engine looks for a text file files/images.txt, which should list the images used in the game
in the form "picture1" (don't use quotes when listing), where there is a corresponding image at images/picture1.png. The engine's java files
can be found in the src folder in com/lpq/game. Just copy the com folder into any project that wants to use it, and import the appropriate classes.

Game Controls:

- Tap Left/Right/Down arrow keys: to move pair of falling balls in that respective direction.
- Tap X: to rotate falling balls clockwise.
- Tap Z: to rotate falling balls counter-clockwise.
- Enter: Pause/Un-Pause game


Game Notes:

- Each ball popped in a chain is worth 50 points. This value goes up with each additional chain combo performed in that round.
- The game is over once a ball is stacked too high and ends up landing off-screen.
- Rotation will be ignored if you attempt to rotate one of the balls impossibly (ie: through another ball or off the screen).
- Enjoy!


Thanks!
Contact me: LiquidProQuoDev@gmail.com
Git Source Repository: github.com/LiquidProQuo/2DGameEngine-Java