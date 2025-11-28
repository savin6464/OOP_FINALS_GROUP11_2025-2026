# Project Assessment Rubric

**Project:** WASD Combat Game
**Section:** C2A
**Course:** Object Oriented Programming

## Grading Breakdown

### 1. Class Diagram Completeness (25%)
**Score:** 1/5
**Notes:** No class diagram found in the project. While the project includes comprehensive documentation explaining OOP concepts in the README.md and OOP_Document_Finals_Group11_C1A.txt files, there is no visual UML or class diagram showing the relationships between GamePanel, Goblin, and SimpleWASDCombatGame classes. The documentation describes a previous text-based game with Character, Warrior, Mage, Archer, and Goblin classes that doesn't match the actual submitted code.

### 2. Java Program - OOP Concepts (50%)
**Score:** 4/5
**Notes:**
- **Encapsulation (Excellent):** Strong encapsulation demonstrated in both GamePanel and Goblin classes. GamePanel uses private fields for game state (px, py, playerHP, map, goblins list) with controlled access through methods. Goblin class encapsulates enemy properties (x, y, hp, speed, attackRange).
- **Abstraction (Good):** GamePanel abstracts complex game logic (collision detection, AI behavior, rendering) behind clean method interfaces like collidesWithTileType(), isGoblinInFrontAndInRange(), and spawnGoblin(). The main class SimpleWASDCombatGame treats GamePanel as a black box.
- **Inheritance (Good):** GamePanel extends JPanel (inheriting Swing functionality) and implements ActionListener and KeyListener interfaces, demonstrating both class inheritance and interface implementation.
- **Polymorphism (Good):** Method overriding shown through implementing interface methods (actionPerformed, keyPressed, keyReleased, paintComponent). The polymorphic clamp() method has two overloaded versions for double and int types.

The code demonstrates solid OOP principles with good use of encapsulation, abstraction through method design, inheritance from Swing classes, and polymorphism through interface implementations. However, the project could benefit from using abstract classes or creating a character hierarchy (Player class, Enemy class) to demonstrate more explicit inheritance patterns.

### 3. Git Usage & Collaboration (15%)
**Score:** 3/5
**Notes:** Moderate collaboration with 10 total commits from 2 contributors: savin6464 (9 commits) and Michael Ong (1 commit). The repository shows primarily solo development with minimal team collaboration. While git is being used for version control, the low number of contributors and commits suggests limited collaborative work or that most development happened outside of git.

### 4. Base Grade (10%)
**Score:** 5/5

---

## Final Grade: 73/100

*Assessment generated based on project analysis.*
