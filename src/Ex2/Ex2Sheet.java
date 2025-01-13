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
    public int width() {
        return cells.length;
    }

    @Override
    public int height() {
        return cells[0].length;
    }
    public void set(int x, int y, String c) {
        if (!isIn(x, y)) {
            throw new IllegalArgumentException("Invalid cell coordinates: (" + x + ", " + y + ")");
        }
        cells[x][y] = new SCell(c);
        // Try to evaluate immediately
        try {
            String evaluated = eval(x, y);
            if (!evaluated.equals("ERR_FORM")) {
                cells[x][y].setData(evaluated);
            }
        } catch (Exception e) {
            // Keep original formula if evaluation fails
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

    @Override
    public String value(int x, int y) {
        Cell cell = get(x, y);
        return cell != null ? cell.getData() : "";
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
    @Override
    public String eval(int x, int y)  {
        // Create cell reference string (e.g., "B1")
        String cellRef = (char)('A' + x) + String.valueOf(y);

        // Check for circular reference
        if (!evaluationStack.add(cellRef)) {
            return "ERR_CIRCULAR";  // Circular reference detected
        }

        try {
            Cell cell = get(x, y);
            if (cell == null) return "";

            String data = cell.getData();
            int type = cell.getType();

            if (type == Ex2Utils.NUMBER) {
                return data;
            } else if (type == Ex2Utils.TEXT) {
                return data;
            } else if (type == Ex2Utils.FORM) {
                if (data.startsWith("=")) {
                    return evaluateFormula(data.substring(1));
                }
            }
            return "ERR_FORM";
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // Always remove the cell from evaluation stack when done
            evaluationStack.remove(cellRef);
        }
    }

    private String evaluateFormula(String formula) throws Exception {
        formula = formula.toUpperCase().trim();

        // Check if it's a simple cell reference (e.g., "B1")
        if (formula.matches("[A-Z]+[0-9]+")) {
            return evaluateCellReference(formula);
        }

        // Handle complex formulas with cell references (e.g., "B1+C1")
        try {
            // Replace all cell references with their values
            String evaluatedFormula = replaceCellReferences(formula);
            Double result = SCell.computeForm("=" + evaluatedFormula);
            return result != null ? result.toString() : "ERR_FORM";
        } catch (Exception e) {
            return "ERR_FORM";
        }
    }

    private String evaluateCellReference(String ref) throws Exception {
        int refX = CellEntry.getColumn(ref);
        int refY = CellEntry.getRow(ref);

        if (!isIn(refX, refY)) {
            return "ERR_REF";
        }

        return eval(refX, refY);
    }

    private String replaceCellReferences(String formula) throws Exception {
        StringBuilder result = new StringBuilder();
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);

            if (Character.isLetterOrDigit(c)) {
                currentToken.append(c);
            } else {
                // Process the accumulated token
                if (currentToken.length() > 0) {
                    String token = currentToken.toString();
                    if (token.matches("[A-Z]+[0-9]+")) {
                        // It's a cell reference, evaluate it
                        String value = evaluateCellReference(token);
                        result.append(value);
                    } else {
                        // It's probably a number or other token
                        result.append(token);
                    }
                    currentToken.setLength(0);
                }
                result.append(c);  // Add the operator or other character
            }
        }

        // Process the last token if exists
        if (currentToken.length() > 0) {
            String token = currentToken.toString();
            if (token.matches("[A-Z]+[0-9]+")) {
                String value = evaluateCellReference(token);
                result.append(value);
            } else {
                result.append(token);
            }
        }

        return result.toString();
    }

    /*  @Override
      public void save(String fileName) throws IOException {
          try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
              writer.write(width() + "," + height());
              writer.newLine();

              for (int x = 0; x < width(); x++) {
                  for (int y = 0; y < height(); y++) {
                      Cell cell = get(x, y);
                      if (cell != null) {
                          writer.write(x + "," + y + "," + cell.getData());
                          writer.newLine();
                      }
                  }
              }
          }*/
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

   /* public void load(String fileName) throws Exception {
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
*/

    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String[] dimensions = reader.readLine().split(",");
            int width = Integer.parseInt(dimensions[0]);
            int height = Integer.parseInt(dimensions[1]);
            cells = new SCell[width][height];

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                String data = parts[2];
                set(x, y, data);
            }
        }
    }
}


