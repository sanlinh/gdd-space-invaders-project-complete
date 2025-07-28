This is a school project by a group of two students named San Lin Htet and Sai Naing Yi Tun
# Space Invaders Game
## Game Rules
 
- Destroy 50 aliens  to reaach the second stage
- Second stage finish when ememy boss die
- to kill enemy boss have to shot 60
- Avoid enemy bombs
- Don't let aliens reach the ground, or it's game over
- Player dies if hit by an enemy bomb## Game Rules
 
- Destroy 50 aliens  to reaach the second stage
- Second stage finish when ememy boss die
- to kill enemy boss have to shot 60
- Avoid enemy bombs
- Don't let aliens reach the ground, or it's game over
- Player dies if hit by an enemy bomb


Player Mechanics:
 
The player controls a spaceship that can move horizontally and shoot projectiles.
Multiple shot types (single, double, triple, and quad shots) are available through power-ups.
The player can collect power-ups to enhance speed or firepower.
Enemy Types:
 
Alien1: Basic enemy with predictable movement.
Alien2: Advanced enemy with bombs and more aggressive behavior.
Boss: A challenging enemy with high health, and special attacks.

Boss Mechanics:
 
The boss moves in a zigzag pattern with occasional dashes.
It shoots projectiles at regular intervals with a cooldown system.
The boss has a health bar and explodes upon defeat.

Power-Ups:
 
SpeedUp: Increases the player's movement speed.
TripleShotPowerUp: Allows the player to shoot three projectiles at once.

Game Progression:
 
The game consists of multiple stages:
Scene1: The player fights waves of Alien1 and Alien2 enemies.
Scene2: The player faces the boss after defeating all enemies in Scene1.
The game transitions from Scene1 to Scene2 upon victory.

Sound Effects:
 
Explosions, shooting, and other in-game actions are accompanied by sound effects using the AudioPlayer class.

Dynamic Spawning:
 
Enemies and power-ups spawn dynamically based on a CSV file (scene2_spawns.csv), allowing for randomized and balanced gameplay.


Graphics:
 
Smooth animations for enemies and the boss.
Explosion effects for destroyed enemies and the boss.