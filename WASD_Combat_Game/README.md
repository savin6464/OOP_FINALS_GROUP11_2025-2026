IN ORDER TO PLAY
1.OPEN IT IN VS-CODE
2.CLICK
3.GO TO SEARCH TYPE : MAIN
4.ON TOP OF THE MAIN SYNTAX CLICK : RUN MAIN



# WELCOME! Simple WASD Combat Game

This is a basic top-down combat game built with **Java** (using the Swing library).

## 🎮 How to Play

| Key | Action |
| :--- | :--- |
| **W A S D** | Move |
| **SHIFT** | Run (Faster movement) |
| **SPACE** | Attack |
| **ENTER** | Spawn an enemy (Goblin) |
| **ESC** | Exit Game |

## 💻 Running the Game in VS Code

1.  Open Project:** Open the `WASD_Combat_Game` folder in VS Code.
2.  Run File: Open the file **`SimpleWASDCombatGame.java`**.
3.  Start Game: Click the **"Run"** button that appears right above the line `public static void main(String[] args)`.

---

File Structure:
The game is made of three files that must be in the same folder:
* `SimpleWASDCombatGame.java` (The main application)
* `GamePanel.java` (The main game logic)
* `Goblin.java` (The enemy unit)
_________________________________________________________________________________
Important note: 

The game's structure is built around the four fundamental principles of OOP: Encapsulation, Abstraction, Inheritance, and Polymorphism.

1. Encapsulation
Encapsulation is the practice of bundling data (variables) and the methods (functions) that operate on that data into a single unit (class), and restricting direct access to the component's internal state.

In the Code: Both the GamePanel and Goblin classes demonstrate this.

Variables like the Goblin's position (x, y), health (hp), and size are kept within the Goblin class.

The GamePanel manages its internal state (like the player's position px, py, and control flags up, down, running) using private access modifiers, preventing other classes from accidentally corrupting the game state.

2. Abstraction
Abstraction is hiding complex implementation details and showing only the essential information to the outside world.

In the Code: The main application class, SimpleWASDCombatGame, works with the GamePanel as a simple, abstract object.

The main class only needs to know that GamePanel is a panel that can be added to the window. It doesn't need to know how GamePanel handles the map generation, collision checks, enemy AI, or drawing—that complexity is hidden.

3. Inheritance
Inheritance is when one class derives properties and characteristics (methods and fields) from another class. This promotes code reusability.

In the Code: The core game class, GamePanel, inherits from two abstract Swing classes:

public class GamePanel extends **JPanel**: It inherits all the basic functionality of a standard Swing panel (like drawing surfaces and being added to a frame).

implements **ActionListener**, **KeyListener**: By implementing these interfaces, GamePanel promises to define specific methods (like actionPerformed and keyPressed), which means it inherits the contract of these interfaces to handle events.

4. Polymorphism
Polymorphism (meaning "many forms") is the ability of a single interface to have multiple implementations. In Java, this often involves method overriding or implementing interfaces.

In the Code: This is primarily shown through the Interface Implementation in GamePanel:

The ActionListener interface requires an actionPerformed(ActionEvent e) method. The GamePanel polymorphically defines this method to execute the game's core logic (movement, collisions, attacks) every time the Timer triggers an ActionEvent. The Timer only knows it's calling an actionPerformed method, but the GamePanel provides the game's specific, unique implementation.