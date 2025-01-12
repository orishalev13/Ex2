package Ex2;
// Add your documentation below:

public class CellEntry  implements Index2D {
    private final int x;
    private final int y;

    public CellEntry(String entry) {
          if (isValid()) {
              throw new IllegalArgumentException("Invalid index values: x must be  between 0 and 25, y must be between 0 and 99.");
          }
        this.x = getColumn(entry);
        this.y = getRow(entry);
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
  /*  public static CellEntry fromString(String indexStr) {
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
    }/**/
    public static int getColumn(String entry) {
        int column = 0;
        for (int i = 0; i < entry.length(); i++) {
            char c = entry.charAt(i);
            if (Character.isDigit(c)) break;
            column = column * 26 + (c - 'A' + 1);
        }
        return column - 1; // Convert to 0-based index
    }

    public static int getRow(String entry) {
        int index = 0;
        while (index < entry.length() && !Character.isDigit(entry.charAt(index))) {
            index++;
        }
        return Integer.parseInt(entry.substring(index)) - 1; // Convert to 0-based index
    }


}