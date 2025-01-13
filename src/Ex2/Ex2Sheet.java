package Ex2;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Ex2Sheet implements Sheet {
    private SCell[][] cells;

    public Ex2Sheet(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("the width and the height >=0");
        }
        cells = new SCell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new SCell("");
            }
        }
    }
    @Override
    public boolean isIn(int x, int y) {
        return x >= 0 && x < width() && y >= 0 && y < height();
    }
    @Override
    public int width() {return cells.length;}
    @Override
    public int height() {
        return cells[0].length;
    }
  @Override
  public void set(int x, int y, String c) {
      if (!isIn(x, y)) {
          throw new IllegalArgumentException("Invalid cell coordinates: (" + x + ", " + y + ")");
      }

      // Store the original formula/value
      String oldValue = cells[x][y].getData();
      cells[x][y] = new SCell(c);

      // If the value actually changed, we need to re-evaluate all cells
      if (oldValue == null || !oldValue.equals(c)) {
          // First evaluate this cell if it's a formula
          if (c != null && c.startsWith("=")) {
              try {
                  eval(x, y);
              } catch (Exception e) {
                  // Handle evaluation error
              }
          }

          // Then re-evaluate all other cells that might depend on this one
          for (int i = 0; i < width(); i++) {
              for (int j = 0; j < height(); j++) {
                  // Skip the current cell
                  if (i == x && j == y) continue;

                  SCell cell = cells[i][j];
                  if (cell != null && cell.getData() != null && cell.getData().startsWith("=")) {
                      String cellData = cell.getData();
                      // If this cell references our changed cell
                      String changedCellRef = String.format("%c%d", (char)('A' + x), y);
                      if (cellData.toUpperCase().contains(changedCellRef)) {
                          try {
                              // Clear evaluation stack before each evaluation
                              evaluationStack.clear();
                              // Re-evaluate the dependent cell
                              String result = eval(i, j);
                          } catch (Exception e) {
                              // Handle evaluation error
                          }
                      }
                  }
              }
          }
      }
  }
    private void reEvaluateDependentCells() {
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                SCell cell = cells[i][j];
                if (cell != null && cell.getData() != null && cell.getData().startsWith("=")) {
                    // Clear evaluation stack before each cell evaluation
                    evaluationStack.clear();
                    try {
                        String result = eval(i, j);
                        // Don't update the cell's data - keep the formula
                    } catch (Exception e) {
                        // Handle evaluation error
                    }
                }
            }
        }
    }
    @Override
    public Cell get(int x, int y) {
        if (!isIn(x, y)) return null;
        return cells[x][y];
    }
    @Override
    public Cell get(String entry) {
        int x = CellEntry.getColumn(entry);
        int y = CellEntry.getRow(entry);
        return get(x, y);
    }
    private boolean isCellReference(String data) {
        return data != null && data.matches("[A-Za-z]+[0-9][0-9]*");
    }
    @Override
    public void eval() throws Exception {
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                eval(x, y);
            }
        }
    }
    @Override
    public int[][] depth() {
        int w = width();
        int h = height();
        int[][] ans = new int[w][h];

        // אתחול כל העומקים ל- -1 (מציין שעדיין לא חושב)
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                ans[x][y] = -1;
            }
        }

        int depth = 0;
        int count = 0;
        int max = w * h;
        boolean flagC;

        while (count < max) {
            flagC = false;

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    if (ans[x][y] == -1 && canBeComputedNow(x, y, ans)) {
                        ans[x][y] = depth;
                        count++;
                        flagC = true;
                    }
                }
            }

            if (!flagC) break;
            depth++;
        }

        return ans;
    }
    private boolean canBeComputedNow(int x, int y, int[][] ans) {
        Cell cell = get(x, y);
        if (cell == null) return false;

        if (cell.getType() == Ex2Utils.TEXT || cell.getType() == Ex2Utils.NUMBER) {
            return true;
        }

        if (cell.getType() == Ex2Utils.FORM || isCellReference(cell.getData())) {
            List<Cell> dependencies = extractDependencies(cell.getData());
            for (Cell dep : dependencies) {
                int depX = CellEntry.getColumn(dep.getData());
                int depY = CellEntry.getRow(dep.getData());
                if (ans[depX][depY] == -1) {
                    return false;
                }
            }
            return true;
        }

        return true;
    }
    private List<Cell> extractDependencies(String data) {
        List<Cell> dependencies = new ArrayList<>();
        String[] tokens = data.split("[+\\-*/()]");
        for (String token : tokens) {
            token = token.trim();
            if (isCellReference(token)) {
                dependencies.add(new SCell(token));
            }
        }
        return dependencies;
    }
    private Set<String> evaluationStack = new HashSet<>();
    private String evaluateCellReference(String ref) {
        int refX = CellEntry.getColumn(ref);
        int refY = CellEntry.getRow(ref);

        if (!isIn(refX, refY)) {
            return "ERR_REF";
        }

        // Evaluate the referenced cell
        return eval(refX, refY);
    }
    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Iterate over the rows in the sheet
            for (int i = 0; i < cells.length; i++) {
                StringBuilder row = new StringBuilder();

                // Iterate over the cells in each row
                for (int j = 0; j < cells[i].length; j++) {
                    Cell cell = cells[i][j];

                    // Use the cell's content (either formula, value, etc.)
                    String cellValue;
                    if (cell instanceof SCell) {
                        SCell scell = (SCell) cell;
                        if (scell.getData() != null) {
                            // Save the formula if it exists
                            cellValue = scell.getData();
                        } else {
                            // Save the evaluated value or the raw content
                            cellValue = scell.toString();
                        }
                    } else {
                        // General fallback for Cell objects
                        cellValue = cell.toString();
                    }

                    // Add the cell content to the row with escaping logic if needed
                    if (cellValue.contains(",") || cellValue.contains("\"")) {
                        // Escape special characters
                        cellValue = "\"" + cellValue.replace("\"", "\"\"") + "\"";
                    }

                    row.append(cellValue);

                    // Add a comma unless it's the last cell in the row
                    if (j < cells[i].length - 1) {
                        row.append(",");
                    }
                }

                // Write the completed row into the file
                writer.write(row.toString());
                writer.newLine();
            }
        }
        System.out.println("Sheet successfully saved to " + fileName);
    }
    public void load(String fileName) throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int rowIndex = 0;

            // Iterate over rows in the file
            while ((line = reader.readLine()) != null) {
                // Split the line into cells by commas, handling escaped characters
                String[] cells = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (cells.length <= rowIndex || cells[0].length() < cells.length) {
                    throw new IOException("File dimensions do not match sheet dimensions.");
                }

                // Add the parsed values into the table row
                for (int colIndex = 0; colIndex < cells.length; colIndex++) {
                    String cellValue = cells[colIndex];

                    // Remove quotes if the value was escaped
                    if (cellValue.startsWith("\"") && cellValue.endsWith("\"")) {
                        cellValue = cellValue.substring(1, cellValue.length() - 1).replace("\"\"", "\"");
                    }

                    // Set data to the table
                    this.cells[rowIndex][colIndex].setData(cellValue);
                }

                rowIndex++;
            }

            // Check if the number of lines matches the height of the table
            if (rowIndex > cells.length) {
                throw new IOException("File contains more rows than sheet dimensions allow.");
            }
        }

        // Recalculate values after loading
        eval();
    }
    @Override
    public String value(int x, int y) {
        Cell cell = get(x, y);
        if (cell == null) return "";

        String data = cell.getData();
        if (data == null || data.isEmpty()) return "";

        // If it's a formula, return the evaluated result
        if (cell.getType() == Ex2Utils.FORM) {
            return eval(x, y);
        }

        // Otherwise return the raw data
        return data;
    }
    @Override
    public String eval(int x, int y) {
        String cellRef = (char)('A' + x) + String.valueOf(y);

        if (!evaluationStack.add(cellRef)) {
            return "ERR_CIRCULAR";
        }

        try {
            Cell cell = get(x, y);
            if (cell == null) return "";

            String data = cell.getData();
            if (data == null || data.isEmpty()) return "";

            // If it's a formula, evaluate it
            if (data.startsWith("=")) {
                return evaluateFormula(data.substring(1));
            }

            // For non-formulas, do type-specific conversion
            if (cell.getType() == Ex2Utils.NUMBER) {
                try {
                    return String.valueOf(Double.parseDouble(data));
                } catch (NumberFormatException e) {
                    return data;
                }
            }

            return data;

        } finally {
            evaluationStack.remove(cellRef);
        }
    }
    private String evaluateFormula(String formula) {
        formula = formula.toUpperCase().trim();

        // Check if it's a direct cell reference
        if (formula.matches("[A-Z]+[0-9]+")) {
            int refX = CellEntry.getColumn(formula);
            int refY = CellEntry.getRow(formula);

            if (!isIn(refX, refY)) {
                return "ERR_REF";
            }

            // Get the referenced cell's type and value
            Cell referencedCell = get(refX, refY);
            if (referencedCell == null) return "ERR_REF";

            int refType = referencedCell.getType();
            // Can only reference numbers or formulas
            if (refType == Ex2Utils.TEXT) {
                return "ERR_FORM";
            }

            return value(refX, refY);
        }

        // Handle complex formulas
        try {
            String evaluated = replaceCellReferences(formula);
            if (evaluated.equals("ERR_REF") || evaluated.equals("ERR_CIRCULAR") || evaluated.equals("ERR_FORM")) {
                return evaluated;
            }

            Double result = SCell.computeForm("=" + evaluated);
            return result != null ? String.valueOf(result) : "ERR_FORM";
        } catch (Exception e) {
            return "ERR_FORM";
        }
    }

    private String replaceCellReferences(String formula) {
        StringBuilder result = new StringBuilder();
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);

            if (Character.isLetterOrDigit(c)) {
                currentToken.append(c);
            } else {
                if (currentToken.length() > 0) {
                    String token = currentToken.toString();
                    if (token.matches("[A-Z]+[0-9]+")) {
                        // Check if we're referencing a text cell
                        int refX = CellEntry.getColumn(token);
                        int refY = CellEntry.getRow(token);
                        Cell referencedCell = get(refX, refY);

                        if (referencedCell != null && referencedCell.getType() == Ex2Utils.TEXT) {
                            return "ERR_FORM";  // Cannot reference text cells
                        }

                        String value = evaluateCellReference(token);
                        if (value.equals("ERR_REF") || value.equals("ERR_CIRCULAR") || value.equals("ERR_FORM")) {
                            return value;
                        }
                        result.append(value);
                    } else {
                        result.append(token);
                    }
                    currentToken.setLength(0);
                }
                result.append(c);
            }
        }

        // Handle the last token
        if (currentToken.length() > 0) {
            String token = currentToken.toString();
            if (token.matches("[A-Z]+[0-9]+")) {
                // Check if we're referencing a text cell
                int refX = CellEntry.getColumn(token);
                int refY = CellEntry.getRow(token);
                Cell referencedCell = get(refX, refY);

                if (referencedCell != null && referencedCell.getType() == Ex2Utils.TEXT) {
                    return "ERR_FORM";  // Cannot reference text cells
                }

                String value = evaluateCellReference(token);
                if (value.equals("ERR_REF") || value.equals("ERR_CIRCULAR") || value.equals("ERR_FORM")) {
                    return value;
                }
                result.append(value);
            } else {
                result.append(token);
            }
        }

        return result.toString();
    }
}
