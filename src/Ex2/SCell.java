package Ex2;

import java.util.ArrayList;
import java.util.List;

public class SCell implements Cell {
    private String data; // Stores the cell's data (formula, number, or text)
    private int type;    // Stores the type of the cell (TEXT, NUMBER, FORM)
    private int order;   // Stores the evaluation order (used for dependency resolution)

    // Constructor to initialize the cell with data
    public SCell(String data) {
        this.data = data != null ? data : ""; // Ensure data is not null
        this.type = getType(); // Determine the type based on the data
    }

    // Default constructor initializes the cell with empty data
    public SCell() {
        this("");
    }

    // Get the cell's data
    @Override
    public String getData() {
        return data;
    }

    // Set the cell's data and update its type
    @Override
    public void setData(String s) {
        this.data = s;
        this.type = getType(); // Update type when data changes
    }

    // Determine the type of the cell based on its data
    @Override
    public int getType() {
        if (data == null || data.isEmpty()) {
            return Ex2Utils.TEXT; // Empty cells are treated as text
        }

        // Convert to uppercase for consistent checking
        String upperData = data.toUpperCase();

        // Check if the data is a formula (starts with "=")
        if (upperData.startsWith("=")) {
            // Check if it's a cell reference (e.g., "=A1")
            String formula = upperData.substring(1);
            if (formula.matches("[A-Z]+[1-9][0-9]*")) {
                return Ex2Utils.FORM; // Cell reference formula
            }
            return Ex2Utils.FORM; // Other formulas
        }

        // Check if the data is a number
        if (isNumber(data)) {
            return Ex2Utils.NUMBER;
        }

        // Default to text if not a formula or number
        return Ex2Utils.TEXT;
    }

    // Set the type of the cell (used for manual type assignment)
    @Override
    public void setType(int t) {
        this.type = t;
    }

    // Get the evaluation order of the cell
    @Override
    public int getOrder() {
        return order;
    }

    // Set the evaluation order of the cell
    @Override
    public void setOrder(int t) {
        this.order = t;
    }

    // Return a string representation of the cell
    @Override
    public String toString() {
        return "SCell{data='" + data + "', type=" + type + "}";
    }

    // Check if a string represents a valid number
    public static boolean isNumber(String cellValue) {
        if (cellValue == null || cellValue.isEmpty()) return false;
        try {
            Double.parseDouble(cellValue); // Try parsing the string as a double
            return true;
        } catch (NumberFormatException e) {
            return false; // Not a number
        }
    }

    // Check if a string represents text (not a number or formula)
    public boolean isText(String cellValue) {
        if (cellValue == null || cellValue.isEmpty()) return false;
        return !isNumber(cellValue) && !isnumform(cellValue) && !isCellform(cellValue);
    }

    // Check if a string represents a numeric formula (e.g., "=5+3")
    public static boolean isnumform(String cellValue) {
        // Basic validation
        if (cellValue == null || cellValue.isEmpty() || !cellValue.startsWith("="))
            return false;

        String formula = cellValue.substring(1); // Remove the "="

        // Check if it's a simple mathematical expression (e.g., "5+3")
        if (formula.matches("\\d+[+\\-*/]\\d+"))
            return true;

        // Check if it's a single number (e.g., "=5")
        try {
            Double.parseDouble(formula);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Check if a string represents a valid mathematical expression
    private static boolean isValidExpression(String expression) {
        // Check for basic format: number operator number
        return expression.matches("\\d+[+\\-*/]\\d+");
    }

    // Extract the operator from a mathematical expression
    private static char extractOperator(String expression) throws Exception {
        for (char c : expression.toCharArray()) {
            if ("+-*/".indexOf(c) != -1) {
                return c; // Return the first valid operator found
            }
        }
        throw new Exception("No valid operator found");
    }

    // Perform a mathematical operation
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

    // Check if a string represents a cell reference formula (e.g., "=A1")
    public static boolean isCellform(String s) {
        if (s == null || s.isEmpty() || s.charAt(0) != '=') return false;
        String cellRef = s.substring(1); // Remove the "="
        return cellRef.matches("[A-Za-z]+[1-9][0-9]*"); // Match cell reference pattern
    }

    // Compute the value of a formula
    public static Double computeForm(String cellValue) {
        try {
            if (cellValue == null || !cellValue.startsWith("=")) {
                return null; // Not a formula
            }

            // Remove '=' and whitespace
            String formula = cellValue.substring(1).replaceAll("\\s+", "");

            return evaluateExpression(formula); // Evaluate the formula
        } catch (Exception e) {
            return null; // Return null on error
        }
    }

    // Evaluate a mathematical expression
    private static Double evaluateExpression(String expression) {
        try {
            // First handle parentheses recursively
            while (expression.contains("(")) {
                int openIndex = expression.lastIndexOf("(");
                int closeIndex = expression.indexOf(")", openIndex);
                if (closeIndex == -1) return null; // Mismatched parentheses

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
                if (part.equals("-") && (i == 0 || "+-*/".contains(parts[i - 1]))) {
                    if (i + 1 < parts.length) {
                        cleanParts.add("-" + parts[i + 1]);
                        i++;
                    }
                } else {
                    cleanParts.add(part);
                }
            }
            parts = cleanParts.toArray(new String[0]);

            if (parts.length == 1) {
                return Double.parseDouble(parts[0]); // Single number
            }

            // Handle multiplication and division first
            List<String> tempParts = new ArrayList<>();
            for (int i = 0; i < parts.length; i++) {
                if (i + 2 < parts.length && (parts[i + 1].equals("*") || parts[i + 1].equals("/"))) {
                    double left = Double.parseDouble(parts[i]);
                    double right = Double.parseDouble(parts[i + 2]);
                    double result;

                    if (parts[i + 1].equals("*")) {
                        result = left * right;
                    } else {
                        if (right == 0) return null; // Division by zero
                        result = left / right;
                    }

                    tempParts.add(String.valueOf(result));
                    i += 2;
                } else if (i + 1 < parts.length && (parts[i + 1].equals("*") || parts[i + 1].equals("/"))) {
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
            return null; // Invalid number format
        } catch (Exception e) {
            return null; // Other errors
        }
    }
}