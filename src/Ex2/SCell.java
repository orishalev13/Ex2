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
  /*  @Override
    public int getType() {
        if (isNumber(data)) return Ex2Utils.NUMBER;
        if (isText(data)) return Ex2Utils.TEXT;
        if (isnumform(data)) return Ex2Utils.FORM;
        return -1; // Unknown type
    }
*/
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

  /*  public static boolean isnumform(String cellValue) {
        if (cellValue == null || cellValue.isEmpty() || cellValue.charAt(0) != '=') return false;
        String formula = cellValue.substring(1);
        try {
            Double.parseDouble(formula); // Check if it's a simple number formula
            return true;
        } catch (NumberFormatException e) {
            return isCellform(formula) || isValidExpression(formula);
        }
    }*/
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

   /* private static boolean isValidExpression(String expression) {
        // Add logic to validate complex expressions if needed
        return true;
    }
*/
    public static boolean isCellform(String s) {
        if (s == null || s.isEmpty() || s.charAt(0) != '=') return false;
        String cellRef = s.substring(1);
        return cellRef.matches("[A-Za-z]+[1-9][0-9]*");
    }
    public static Double computeForm(String cellValue) throws Exception {
        if (cellValue == null || cellValue.isEmpty()) {
            throw new Exception("Formula cannot be empty");
        }

        cellValue = cellValue.replaceAll(" ", ""); // Remove whitespace

        if (cellValue.charAt(0) != '=') {
            throw new Exception("Formula must start with =");
        }

        String formula = cellValue.substring(1);

        // Handle expressions like "5+3" or "B1+C1" (after cell references are replaced)
        if (formula.matches("\\-?\\d+(\\.\\d+)?([+\\-*/]\\-?\\d+(\\.\\d+)?)*")) {
            return evaluateExpression(formula);
        }

        // If it's just a single number
        if (isNumber(formula)) {
            return Double.parseDouble(formula);
        }

        throw new Exception("Invalid formula: " + cellValue);
    }

    private static Double evaluateExpression(String expression) throws Exception {
        // Split the expression into numbers and operators
        String[] numbers = expression.split("[+\\-*/]");
        List<Character> operators = new ArrayList<>();

        for (char c : expression.toCharArray()) {
            if ("+-*/".indexOf(c) >= 0) {
                operators.add(c);
            }
        }

        // Start with the first number
        double result = Double.parseDouble(numbers[0]);

        // Apply each operator in sequence
        for (int i = 0; i < operators.size(); i++) {
            double nextNum = Double.parseDouble(numbers[i + 1]);
            char op = operators.get(i);

            switch (op) {
                case '+': result += nextNum; break;
                case '-': result -= nextNum; break;
                case '*': result *= nextNum; break;
                case '/':
                    if (nextNum == 0) throw new Exception("Division by zero");
                    result /= nextNum;
                    break;
                default: throw new Exception("Invalid operator: " + op);
            }
        }

        return result;
    }


    private static double evaluateCellReference(String ref) throws Exception {
        // כאן ניתן להוסיף לוגיקה לחישוב הפניות לתאים
        throw new Exception("חישוב הפניות לתאים לא ממומש.");
    }
   /* public static Double computeForm(String cellValue) throws Exception {
        if (cellValue == null || cellValue.isEmpty())
            throw new Exception("the formula incorrect");
        cellValue = cellValue.replaceAll(" ", "");
        double sum = -1;
        if (isNumber(cellValue))
            return Double.parseDouble(cellValue);
        if (isnumform(cellValue))
            return sum = evaluateExpression(cellValue);
        //if()
        // return computeForm();
        return (double) -1;

    }

    public static int indOflast(String str) {

        int p = 0;
        for (int s = str.length() - 1; s >= 0; s--) {
            if (str.charAt(s) == ')')
                while (str.charAt(s) != '(') {
                    if (s == 0) return s;
                    s--;
                    if (p == 0) {
                        if ("/*".contains(str.charAt(s) + ""))
                            p = s;
                        if ("-+".contains(str.charAt(s) + ""))
                            return s;
                    }
                }

            if ("/*".contains(str.charAt(s) + ""))
                p = s;
            if ("-+".contains(str.charAt(s) + ""))
                return s;
        }
        return p;
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    public static double evaluateExpression(String expression) throws Exception {
        if (expression == null || expression.isEmpty())
            throw new Exception("the formula incorrect");
        if (expression.charAt(0) == '=')
            expression = expression.substring(1);
        if (isNumber(expression))
            return Double.parseDouble(expression);
        else {
            int i = indOflast(expression);
            if (i == 0)
                expression = remove(expression);
            if (isNumber(expression))
                return Double.parseDouble(expression);
            char c = expression.charAt(i);
            return applyOperator(evaluateExpression(expression.substring(0, i)), evaluateExpression(expression.substring(i + 1)), c);
        }

    }
*/
    public static String remove(String str) {
        for (int i = 0; i < str.length(); i++) {
            if ("()".contains(str.charAt(i) + "")) {
                char c = str.charAt(i);
                str = str.replace(Character.toString(c), "");
            }
        }
        return str;
    }

    private static double applyOperator(double a, double b, char operator) throws Exception {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> a / b;
            default -> throw new Exception("the operatror incorrect");
        };
    }
}