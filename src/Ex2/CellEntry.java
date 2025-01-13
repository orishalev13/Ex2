package Ex2;
// Add your documentation below:

import java.util.ArrayList;

public class CellEntry  implements Index2D {
    private String[][] table = new String[Ex2Utils.WIDTH][Ex2Utils.HEIGHT];
    private final int x;
    private final int y;
    private String stringindex;

         public CellEntry(int x, int y) {
             //Grid init in case params xy
             for (int i = 0; i < Ex2Utils.WIDTH; i++) {
                 for (int j = 0; j < Ex2Utils.HEIGHT; j++) {
                     char letter = (char) (i + 65);
                     this.table[i][j] = String.valueOf((letter)) + j;
                 }
             }
             this.x = x;
             this.y = y;
             stringindex = this.toString();
         }
    public CellEntry(String cellName) {
        cellName = cellName.toUpperCase();
        this.stringindex = cellName;
        if (isValid()) {
            for (int i = 0; i < Ex2Utils.WIDTH; i++) {
                for (int j = 0; j < Ex2Utils.HEIGHT; j++) {
                    char letter = (char) (i + 65);
                    this.table[i][j] = String.valueOf((letter)) + j;
                }
            }
            ArrayList<Integer> result = toSheetind();
            //System.out.println(result);
            this.x = result.get(0);
            this.y = result.get(1);
        }else
            throw new IllegalArgumentException("Illigal coords");
    }
    @Override
    public boolean isValid() {
        // Ensure x is between 0 and 25 (A-Z), and y is between 0 and 99.
        return x >= 0 && x <= 25 && y >= 0 && y <= 99;
    }
    public ArrayList<Integer> toSheetind() {
        ArrayList<Integer> result = new ArrayList<>();
        int x = (int)stringindex.charAt(0) - 65;
        int y = Integer.parseInt(stringindex.substring(1));
        result.add(x);
        result.add(y);
        return result;
    }


    @Override
    public int getX() {
            return x;
    }

    @Override
    public int getY() {
            return y;
    }

   /* public static int getColumn(String entry) {
        int column = 0;
        for (int i = 0; i < entry.length(); i++) {
            char c = entry.charAt(i);
            if (Character.isDigit(c)) break;
            column = column * 26 + (c - 'A' + 1);
        }
        return column - 1; // Convert to 0-based index
    }
*/
   /* public static int getRow(String entry) {
        int index = 0;
        while (index < entry.length() && !Character.isDigit(entry.charAt(index))) {
            index++;
        }
        return Integer.parseInt(entry.substring(index)) - 1; // Convert to 0-based index
    }*/


    public static int getRow(String entry) {
        // Skip over any letters at the start
        int index = 0;
        while (index < entry.length() && Character.isLetter(entry.charAt(index))) {
            index++;
        }

        // Parse the number part directly - no need to subtract 1
        // since we want 0-based indexing (B0 is first row)
        try {
            String numberPart = entry.substring(index);
            return Integer.parseInt(numberPart);  // Don't subtract 1
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid row number in: " + entry);
        }
    }

    public static int getColumn(String entry) {
        entry = entry.toUpperCase();

        int column = 0;
        int i = 0;
        while (i < entry.length() && Character.isLetter(entry.charAt(i))) {
            column = column * 26 + (entry.charAt(i) - 'A' + 1);
            i++;
        }
        return column - 1;  // Still need to convert to 0-based for columns
    }
}