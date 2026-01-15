# Maze Game - Neurological Rehabilitation Tool

## Project Overview
This project is a specialized Java application designed as a therapeutic tool for individuals with neurological impairments, such as those recovering from strokes or living with cerebral palsy. The primary objective of the software is to aid in the rehabilitation of fine motor skills and hand-eye coordination by enforcing strict cursor control within a procedurally generated environment.

Unlike standard maze games, this application utilizes `java.awt.Robot` to strictly manage cursor positioning, preventing cheating and forcing the user to develop precise motor control to navigate from the start point to the end point without colliding with walls.

## Technical Specifications
* **Language:** Java (JDK 17 or higher)
* **GUI Framework:** Java Swing & AWT
* **Rendering:** Custom `Graphics2D` rendering with `AlphaComposite` for layer management.
* **Audio:** Java Sound API implementation.

## Key Algorithms & Logic
The core of the procedural generation relies on the **Randomized Depth-First Search (DFS)** algorithm with backtracking. This ensures that:
1.  Every generated maze is solvable (a valid path exists).
2.  Every maze layout is unique.
3.  The complexity scales mathematically with the grid size (15x15, 25x25, 35x35).

## Features
* **Procedural Content Generation:** Mazes are generated in real-time using graph traversal algorithms.
* **Dynamic Difficulty Scaling:** The user can select from three complexity levels, which adjust the grid density and visual assets accordingly.
* **Collision Detection:** Real-time monitoring of mouse coordinates via `MouseMotionListener` to detect wall collisions immediately.
* **Input Control:** Utilization of the `Robot` class to reset cursor position, enforcing continuous, valid movement.

## Setup and Execution
To run the application locally, ensure you have the Java Development Kit (JDK) installed.

1.  Compile the source code:
    ```bash
    javac MazeGame.java
    ```

2.  Run the application:
    ```bash
    java MazeGame
    ```

## Author
**Grzegorz Bach**
Contact: grzegorz.bach02@gmail.com
