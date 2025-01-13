package Ex2;

import java.util.ArrayList;
import java.util.List;



public class SCell implements Cell {
    private String data;
    private int type;
    private int order;

    public SCell(String data) {
        this.data = data != null ? data : "";
        this.type = getType();

    }

    public SCell() {
        this("");
    }

    @Override
    public String getData() {
        return data;
    }
    @Override
    public void setData(String s) {
        this.data = s;
        this.type = getType(); // Update type when data changes
    }
    @Override
    public int getType() {
        if (data == null || data.isEmpty()) {
            return Ex2Utils.TEXT;
        }

        // Convert to uppercase for consistent checking
        String upperData = data.toUpperCase();

        if (upperData.startsWith("=")) {
            // Check if it's a number formula or cell reference
            String formula = upperData.substring(1);
            if (formula.matches("[A-Z]+[1-9][0-9]*")) {
                return Ex2Utils.FORM;  // Cell reference
            }
            return Ex2Utils.FORM;  // Other formulas
        }

        if (isNumber(data)) {
            return Ex2Utils.NUMBER;
        }

        return Ex2Utils.TEXT;
    }
    @Override
    public void setType(int t) {
        this.type = t;
    }
    @Override
    public int getOrder() {
        return order;
    }
    @Override
    public void setOrder(int t) {
        this.order = t;
    }
    @Override
    public String toString() {
        return "SCell{data='" + data + "', type=" + type + "}";
    }
    public static boolean isNumber(String cellValue) {
        if (cellValue == null || cellValue.isEmpty()) return false;
        try {
            Double.parseDouble(cellValue);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public boolean isText(String cellValue) {
        if (cellValue == null || cellValue.isEmpty()) return false;
        return !isNumber(cellValue) && !isnumform(cellValue) && !isCellform(cellValue);
    }

  public static boolean isnumform(String cellValue) {
      // Basic validation
      if (cellValue == null || cellValue.isEmpty() || !cellValue.startsWith("="))
          return false;

      String formula = cellValue.substring(1);

      // Check if it's a simple mathematical expression
      if (formula.matches("\\d+[+\\-*/]\\d+"))
          return true;

      // Check if it's a single number
      try {
          Double.parseDouble(formula);
          return true;
      } catch (NumberFormatException e) {
          return false;
      }
  }
    /**
     * Validates if the input string represents a valid mathematical expression
     * @param expression The string to validate
     * @return true if the expression is valid, false otherwise
     */
    private static boolean isValidExpression(String expression) {
        // Check for basic format: number operator number
        return expression.matches("\\d+[+\\-*/]\\d+");
    }
    /**
     * Extracts the operator from a mathematical expression
     * @param expression The mathematical expression
     * @return The operator character
     * @throws Exception if no valid operator is found
     */
    private static char extractOperator(String expression) throws Exception {
        for (char c : expression.toCharArray()) {
            if ("+-*/".indexOf(c) != -1) {
                return c;
            }
        }
        throw new Exception("No valid operator found");
    }
    /**
     * Performs the mathematical operation
     * @param num1 First number
     * @param num2 Second number
     * @param operator The operator to apply
     * @return The result of the operation
     * @throws Exception if the operation is invalid (e.g., division by zero)
     */
    private static double calculateResult(double num1, double num2, char operator) throws Exception {
        switch (operator) {
            case '+': return num1 + num2;
            case '-': return num1 - num2;
            case '*': return num1 * num2;
            case '/':
                if (num2 == 0) throw new Exception("Division by zero");
                return num1 / num2;
            default: throw new Exception("Invalid operator: " + operator);
        }
    }
    public static boolean isCellform(String s) {
        if (s == null || s.isEmpty() || s.charAt(0) != '=') return false;
        String cellRef = s.substring(1);
        return cellRef.matches("[A-Za-z]+[1-9][0-9]*");
    }
    public static Double computeForm(String cellValue) {
        try {
            if (cellValue == null || !cellValue.startsWith("=")) {
                return null;
            }

            // Remove '=' and whitespace
            String formula = cellValue.substring(1).replaceAll("\\s+", "");

            return evaluateExpression(formula);
        } catch (Exception e) {
            return null;
        }
    }

    private static Double evaluateExpression(String expression) {
        try {
            // First handle parentheses recursively
            while (expression.contains("(")) {
                int openIndex = expression.lastIndexOf("(");
                int closeIndex = expression.indexOf(")", openIndex);
                if (closeIndex == -1) return null;  // Mismatched parentheses

                // Evaluate the expression inside parentheses
                String subExpr = expression.substring(openIndex + 1, closeIndex);
                Double subResult = evaluateExpression(subExpr);
                if (subResult == null) return null;

                // Replace the parentheses expression with its result
                expression = expression.substring(0, openIndex) +
                        subResult.toString() +
                        expression.substring(closeIndex + 1);
            }

            // Split the expression by operators, handling negative numbers
            String[] parts = expression.split("(?<=[-+*/])|(?=[-+*/])");

            // Clean up the parts and handle negative numbers
            List<String> cleanParts = new ArrayList<>();
            for (int i = 0; i < parts.length; i++) {
                String part = parts[i].trim();
                if (part.isEmpty()) continue;

                // Handle negative numbers
                if (part.equals("-") && (i == 0 || "+-*/".contains(parts[i-1]))) {
                    if (i + 1 < parts.length) {
                        cleanParts.add("-" + parts[i+1]);
                        i++;
                    }
                } else {
                    cleanParts.add(part);
                }
            }
            parts = cleanParts.toArray(new String[0]);

            if (parts.length == 1) {
                return Double.parseDouble(parts[0]);
            }

            // Handle multiplication and division first
            List<String> tempParts = new ArrayList<>();
            for (int i = 0; i < parts.length; i++) {
                if (i+2 < parts.length && (parts[i+1].equals("*") || parts[i+1].equals("/"))) {
                    double left = Double.parseDouble(parts[i]);
                    double right = Double.parseDouble(parts[i+2]);
                    double result;

                    if (parts[i+1].equals("*")) {
                        result = left * right;
                    } else {
                        if (right == 0) return null; // Division by zero
                        result = left / right;
                    }

                    tempParts.add(String.valueOf(result));
                    i += 2;
                } else if (i+1 < parts.length && (parts[i+1].equals("*") || parts[i+1].equals("/"))) {
                    // Skip, will be handled in next iteration
                } else {
                    tempParts.add(parts[i]);
                }
            }

            // Now handle addition and subtraction
            double result = Double.parseDouble(tempParts.get(0));
            for (int i = 1; i < tempParts.size() - 1; i += 2) {
                double nextNum = Double.parseDouble(tempParts.get(i + 1));
                if (tempParts.get(i).equals("+")) {
                    result += nextNum;
                } else if (tempParts.get(i).equals("-")) {
                    result -= nextNum;
                }
            }

            return result;

        } catch (NumberFormatException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

}
