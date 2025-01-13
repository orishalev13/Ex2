package Ex2;

import java.util.ArrayList;

/**
 * Represents a cell in a spreadsheet using coordinates (x, y) or cell references (e.g., "A1").
 * Implements Index2D for coordinate access and validation.
 */
public class CellEntry implements Index2D {
    private String[][] table = new String[Ex2Utils.WIDTH][Ex2Utils.HEIGHT]; // Stores cell references (e.g., "A0")
    private final int x; // Column index (0-based)
    private final int y; // Row index (0-based)
    private String stringindex; // Cell reference as a string (e.g., "A1")

    // Constructor using coordinates (x, y)
    public CellEntry(int x, int y) {
        // Initialize table with cell references (e.g., "A0", "B0", etc.)
        for (int i = 0; i < Ex2Utils.WIDTH; i++) {
            for (int j = 0; j < Ex2Utils.HEIGHT; j++) {
                char letter = (char) (i + 65); // Convert index to letter (A-Z)
                this.table[i][j] = String.valueOf(letter) + j;
            }
        }
        this.x = x;
        this.y = y;
        this.stringindex = this.toString(); // Store cell reference
    }

    // Constructor using cell reference (e.g., "A1")
    public CellEntry(String cellName) {
        cellName = cellName.toUpperCase(); // Ensure uppercase
        this.stringindex = cellName;

        if (isValid()) {
            // Initialize table with cell references
            for (int i = 0; i < Ex2Utils.WIDTH; i++) {
                for (int j = 0; j < Ex2Utils.HEIGHT; j++) {
                    char letter = (char) (i + 65); // Convert index to letter (A-Z)
                    this.table[i][j] = String.valueOf(letter) + j;
                }
            }

            // Convert cell reference to coordinates
            ArrayList<Integer> result = toSheetind();
            this.x = result.get(0); // Column index
            this.y = result.get(1); // Row index
        } else {
            throw new IllegalArgumentException("Illegal coordinates"); // Invalid cell reference
        }
    }

    // Check if coordinates are valid (0 <= x <= 25, 0 <= y <= 99)
    @Override
    public boolean isValid() {
        return x >= 0 && x <= 25 && y >= 0 && y <= 99;
    }

    // Convert cell reference (e.g., "A1") to coordinates (x, y)
    public ArrayList<Integer> toSheetind() {
        ArrayList<Integer> result = new ArrayList<>();
        int x = (int) stringindex.charAt(0) - 65; // Column index (A=0, B=1, etc.)
        int y = Integer.parseInt(stringindex.substring(1)); // Row index
        result.add(x);
        result.add(y);
        return result;
    }

    // Get column index (x)
    @Override
    public int getX() {
        return x;
    }

    // Get row index (y)
    @Override
    public int getY() {
        return y;
    }

    // Extract row index from cell reference (e.g., "A1" -> 1)
    public static int getRow(String entry) {
        int index = 0;
        while (index < entry.length() && Character.isLetter(entry.charAt(index))) {
            index++; // Skip letters
        }
        try {
            return Integer.parseInt(entry.substring(index)); // Parse row number
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row number in: " + entry);
        }
    }

    // Extract column index from cell reference (e.g., "A1" -> 0)
    public static int getColumn(String entry) {
        entry = entry.toUpperCase(); // Ensure uppercase
        int column = 0;
        int i = 0;
        while (i < entry.length() && Character.isLetter(entry.charAt(i))) {
            column = column * 26 + (entry.charAt(i) - 'A' + 1); // Convert letters to column index
            i++;
        }
        return column - 1; // Convert to 0-based index
    }
}