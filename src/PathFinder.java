import java.io.File; //unused
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PathFinder {

    //counters to display number of steps and number of solutions found vs not found
    static int steps = 0;

    public static Graph createGraph(int[][] gameInterface, int finishI, int finishJ) {
        Graph graph = new Graph(false, true);

        int rows = gameInterface.length;
        int cols = gameInterface[0].length;

        int[][] dirs = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        // Create vertices for each cell in the game interface
        Vertex[][] vertices = new Vertex[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                vertices[i][j] = graph.addVertex(i + "," + j);

            }
        }

        // Add edges between cells in the same direction until an obstacle or the end
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (gameInterface[i][j] == 1) continue; // Skip obstacles

                // For each direction
                for (int[] dir : dirs) {
                    int x = i + dir[0];
                    int y = j + dir[1];

                    // Keep moving in the direction until an obstacle or the end
                    while (x >= 0 && x < rows && y >= 0 && y < cols && gameInterface[x][y] != 1) {
                        x += dir[0];
                        y += dir[1];
                        if (x == finishI && y == finishJ) {
                            //   graph.addEdge(vertices[i][j], vertices[x][y], 1);
                            break;
                        }
                    }

                    // Move back one step to the last valid position
                    if (x != finishI || y != finishJ)  {
                        x -= dir[0];
                        y -= dir[1];
                    }

                    // Add an edge to the last valid position in the direction if finishVertex has not already been added

                    if (x != i || y != j ) {
                        graph.addEdge(vertices[i][j], vertices[x][y], 1);
                    }

                }
            }
        }
        return graph;
    }



    public List<Vertex> findPath(Vertex startVertex, Vertex endVertex) {
        Map<Vertex, Vertex> parentMap = new HashMap<>(); //stores the parent vertex of each visited vertex to help reconstruct the path later
        Queue<Vertex> queue = new LinkedList<>(); //holds the vertices to be explored, follows FIFO principle
        Set<Vertex> visited = new HashSet<>(); //to keep track of visited vertices

        queue.add(startVertex);
        visited.add(startVertex);

        while (!queue.isEmpty()) {
            Vertex currentVertex = queue.poll(); //retrieves and removes the head of the queue

            if (currentVertex.equals(endVertex)) {
                break; // Break out of the BFS loop when the end vertex is found
            }

            for (Edge edge : currentVertex.getEdges()) {
                Vertex neighbor = edge.getEnd();
                if (!visited.contains(neighbor)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                    parentMap.put(neighbor, currentVertex);

                }
            }
        }

        // Reconstruct the path only from the end vertex to the start vertex
        List<Vertex> path = new ArrayList<>();
        Vertex node = endVertex;
        while (node != null) {
            path.add(node);
            node = parentMap.get(node);
        }
        Collections.reverse(path);

        if (path.get(0) != startVertex) {
            // If the start vertex is not reachable from the end vertex, return an empty list
            return new ArrayList<>();
        }

        return path;
    }


    public static void main(String[] args) {
        System.out.println();
        System.out.println("-------------Sliding puzzle - Shortest Path Finder-------------");
        System.out.println();
        while (true) {
            long startTime;
            long runTime;

            int printPuzzle = 0; //to store input of choice of whether to print puzzle or not

            try {
                startTime = System.currentTimeMillis(); // Get the start time
                System.out.println("Copy the file path of the input file and enter it please. (Enter 'exit' to exit the program) ");


                Scanner scanner = new Scanner(System.in);
                String filePath = scanner.nextLine();

                // Loop until a valid file path is entered or the user wants to exit
                while (!filePath.equalsIgnoreCase("exit")) {
                    // Check if the path starts and ends with double quotes
                    if (filePath.startsWith("\"") && filePath.endsWith("\"")) {
                        // Remove the quotes if present
                        filePath = filePath.substring(1, filePath.length() - 1);
                    }
                    // Check if the file exists
                    if (Files.exists(Paths.get(filePath))) {
                        break; // Exit the loop if the file exists
                    } else {
                        System.out.println("The file does not exist. Please enter a valid file path or 'exit' to exit the program.");
                        filePath = scanner.nextLine();
                    }
                }

                // Check if the user wants to exit
                if (filePath.equalsIgnoreCase("exit")) {
                    break; // Exit the loop
                }


                System.out.println("Do you want to print the puzzle as well? Enter 1 to print the puzzle or any other number to not print it.");

                while (!scanner.hasNextInt()) {
                    System.out.println("That's not a valid input! Please enter an integer.");
                    scanner.next(); // discard the non-integer input
                }

                printPuzzle = scanner.nextInt();
                System.out.println();

                Parser maze = new Parser(filePath);


                int[][] gameInterface = maze.getGrid();

                int startI = maze.getStart()[0];
                int startJ = maze.getStart()[1];
                int finishI = maze.getFinish()[0];
                int finishJ = maze.getFinish()[1];

                System.out.println("-------------------------------------------------------------------");
                //  System.out.println("Path of the file is : " + file.getAbsolutePath());
                System.out.println("Path of the file is : " + filePath);
                System.out.println();
                System.out.println("Starting point is : " + (startJ + 1) + ", " + (startI + 1)); //switched and adding one to show the coordinates consistently
                System.out.println("Finishing point is : " + (finishJ + 1) + ", " + (finishI + 1));
                System.out.println();

                //print out the maze before showing the moves if asked for
                if (printPuzzle == 1) {
                    for (int i = 0; i < gameInterface.length; i++) {
                        for (int j = 0; j < gameInterface[i].length; j++) {
                            if (i == startI && j == startJ) {
                                System.out.print("S ");
                            } else if (i == finishI && j == finishJ) {
                                System.out.print("F ");
                            } else {
                                System.out.print(gameInterface[i][j] + " ");
                            }
                        }
                        System.out.println();
                    }
                }

                System.out.println("Creating Graph...");
                System.out.println();
                Graph graph = createGraph(gameInterface, finishI, finishJ);
                System.out.println("Graph created");
                System.out.println();
                Vertex startVertex = graph.getVertices().get(startI * gameInterface[0].length + startJ);
                Vertex finishVertex = graph.getVertices().get(finishI * gameInterface[0].length + finishJ);

                runTime = System.currentTimeMillis();
                PathFinder pathFinder = new PathFinder(); // Create an instance


                List<Vertex> shortestPath = pathFinder.findPath(startVertex, finishVertex); //average runtime of 75 or less ms

                steps = 0;
                if (shortestPath != null && shortestPath.size() > 0) {

                    System.out.println("Shortest Path from start to finish:");
                    int prevX = -1;
                    int prevY = -1;


                    for (int i = 0; i < shortestPath.size(); i++) {
                        steps++;
                        Vertex vertex = shortestPath.get(i);
                        String[] coords = vertex.getData().split(",");
                        int y = Integer.parseInt(coords[0]) + 1; //adding 1 because the positions displayed should start from 1 instead of 0
                        int x = Integer.parseInt(coords[1]) + 1;

                        String direction = "";
                        if (prevX != -1 && prevY != -1) {
                            if (x > prevX) {
                                direction = "Move right to ";
                            } else if (x < prevX) {
                                direction = "Move left to ";
                            } else if (y > prevY) {
                                direction = "Move down to ";
                            } else if (y < prevY) {
                                direction = "Move up to ";
                            }
                        } else {
                            direction = "Start at "; // Handle the first vertex (start)
                        }

                        //here x and y will be swapped because the first value, x denotes the y coordinate and the second valuem y denotes the x coordinate
                        System.out.println((i + 1) + ". " + direction + "(" + x + "," + y + ")");
                        prevX = x;
                        prevY = y;
                    }

                    System.out.println("Number of steps : " + steps);

                } else {
                    System.out.println("No path found.");
                }

            } catch (IOException e) {
                System.out.println("Incorrect file path/ file does not exist");
                throw new RuntimeException(e);
            }

            long endTime = System.currentTimeMillis(); // Get the end time
            long executeDuration = endTime - startTime; // Calculate the duration
            long runtimeDuration = endTime - runTime;
            System.out.println("PathFinder Algorithm runtime : " + runtimeDuration + " milliseconds");
            System.out.println("Program runtime: " + executeDuration + " milliseconds");
            System.out.println();

        }
    }

}


