package Ex2;
// Add your documentation below:

public class CellEntry  implements Index2D {
    private final int x;
    private final int y;

    public CellEntry(int x, int y) {
          if (isValid()) {
              throw new IllegalArgumentException("Invalid index values: x must be >= 0, y must be between 0 and 99.");
          }
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean isValid() {
        // Ensure x is between 0 and 25 (A-Z), and y is between 0 and 99.
        return x >= 0 && x <= 25 && y >= 0 && y <= 99;
    }

    @Override
    public int getX() {
            return x;
    }

    @Override
    public int getY() {
            return y;
    }
    public static CellEntry fromString(String indexStr) {
        if (indexStr == null || indexStr.length() < 3|| indexStr.charAt(0)!='=') {
            throw new IllegalArgumentException("Invalid index format.");
        }
        indexStr=indexStr.substring(1);
        char letter = indexStr.charAt(0);
        if (!Character.isLetter(letter)) {
            throw new IllegalArgumentException("First character must be a letter (A-Z or a-z).");
        }

        int x = Character.toUpperCase(letter) - 'A';
        String numberPart = indexStr.substring(1);

        try {
            int y = Integer.parseInt(numberPart);
            if (y < 0 || y > 99) {
                throw new IllegalArgumentException("Number part must be between 0 and 99.");
            }
            return new CellEntry(x,y);}

         catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in index.", e);
        }
    }

}