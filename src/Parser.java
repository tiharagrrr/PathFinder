import java.io.*;
import java.util.*;

public class Parser {
    private int[][] grid;
    private int[] start;
    private int[] finish;

    public Parser(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }

        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Input file is empty!");
        }

        int rows = lines.size();
        int cols = lines.get(0).length();
        grid = new int[rows][cols];
        start = new int[2];
        finish = new int[2];

        for (int i = 0; i < rows; i++) {
            String arrayLine = lines.get(i); // Store the line for easier access
            if (arrayLine.isEmpty()) {
                // Handle empty line (e.g., skip to next line)
                continue;
            }
            for (int j = 0; j < cols; j++) {
                char c = arrayLine.charAt(j);
                if (c == '.') {
                    grid[i][j] = 0;
                } else if (c == '0') {
                    grid[i][j] = 1;
                } else if (c == 'F') {
                    grid[i][j] = 0;
                    finish[0] = i;
                    finish[1] = j;
                } else if (c == 'S') {
                    grid[i][j] = 0;
                    start[0] = i;
                    start[1] = j;
                }
            }
        }
    }

    public int[][] getGrid() {
        return grid;
    }

    public int[] getStart() {
        return start;
    }

    public int[] getFinish() {
        return finish;
    }
}

