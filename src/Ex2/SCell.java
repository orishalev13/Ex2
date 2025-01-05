package Ex2;
// Add your documentation below:

public class SCell implements Cell {

    private String data;
    private int type;
    private int order;

    // Add your code here
    public SCell(String data, int type, int order) {

        this.data = data;
        this.type = type;
        this.order = order;
    }

    public SCell() {
        this("", 0, 0);
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public void setData(String s) {
        this.data = s;
    }

    @Override
    public int getType() {
        return type;
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
        return "CellImpl{" +
                "data='" + data + '\'' +
                ", type=" + type +
                ", order=" + order +
                '}';
    }

   /* public SCell(String s) {
        // Add your code here
        setData(s);
    }

    @Override
    public int getOrder() {
        return order;
        // Add your code here

        return 0;
        // ///////////////////
    }

    //@Override
    @Override
    public String toString() {
        return getData();
    }

    @Override
public void setData(String s) {
        // Add your code here
        line = s;
        /////////////////////
    }
    @Override
    public String getData() {
        return line;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        type = t;
    }

    @Override
    public void setOrder(int t) {
        this.order = t;
        // Add your code here*/


    public static boolean isNumber(String cellValue) {
        cellValue = cellValue.replaceAll(" ", "");
        if (cellValue == null || cellValue.isEmpty()) {
            return false; // Return false for null or empty strings
        }
        try {
            Double.valueOf(cellValue); // Attempt to parse the string as a double
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isText(String cellValue) {
        cellValue = cellValue.replaceAll(" ", "");
        if (cellValue == null || cellValue.isEmpty()) {
            return false; // Return false for null or empty strings
        }
        if (cellValue.charAt(0) == '=')
            return false;
        if (isForm(cellValue))
            return false;
        if (isNumber(cellValue))
            return false;
        return true;


    }

    public static boolean isForm(String cellValue) {
        cellValue = cellValue.replaceAll(" ", "");//remove all the space
        if (cellValue == null || cellValue.isEmpty()) {
            return false; // Return false for null or empty strings
        }
        if (cellValue.charAt(0) != '=') {
            return false; //   only '=' at the start
        }
        int par = 0;
        boolean s = false;
        for (int i = 1; i < cellValue.length(); i++) {
            if (!Character.isDigit(cellValue.charAt(i)) && !("()+-*/.".contains(cellValue.charAt(i) + "")))// there only char from type numbers or -=\*.()
                return false;
            if (cellValue.charAt(i) == '(' && cellValue.charAt(i + 1) == ')')// there no way of  empty ().
                return false;
            if (cellValue.charAt(i) == '(') //check that no way of )( or just).
                par++;
            if (cellValue.charAt(i) == ')')
                par--;
            if (par < 0)
                return false;
            if ("+-*/".contains(cellValue.charAt(i) + "")) {// check if there ++,--,//,**
                if (s)
                    return false;
                s = true;
                if (i == cellValue.length() - 1)//check if the operator is in the end
                    return false;
            } else s = false;
            if ("+-".contains(cellValue.charAt(i) + "")) { // if there -+ check
                /** if (i < cellValue.length() - 1 && cellValue.charAt(i + 1) == ')')
                 return false;//check if there -) that not correct
                 */
                // Ensure that the operator is not at the edge of the string
                if (i > 1 && i < cellValue.length() - 1) {
                    // Valid characters before the operator: digits, closing parentheses, or opening parentheses
                    if (!Character.isDigit(cellValue.charAt(i - 1)) && cellValue.charAt(i - 1) != ')' && cellValue.charAt(i - 1) != '(') {
                        return false;
                    }
                    // Valid characters after the operator: digits or opening parentheses
                    if (!Character.isDigit(cellValue.charAt(i + 1)) && cellValue.charAt(i + 1) != '(') {
                        return false;
                    }
                } else {
                    return false; // An operator at the edge of the string is invalid
                }
            }

            if ("/*".contains(cellValue.charAt(i) + "")) {
                if (i < cellValue.length() - 1 && cellValue.charAt(i + 1) == ')')
                    return false;//check if there -) that not correct
                if (i > 0 && (Character.isDigit(cellValue.charAt(i - 1)) || "()".contains(cellValue.charAt(i - 1) + ""))) {
                    continue;
                }
                if (i < cellValue.length() - 1 && (Character.isDigit(cellValue.charAt(i + 1)) || cellValue.charAt(i + 1) == '(')) {
                    continue;
                }
                return false;
            }
            if (cellValue.charAt(i) == '.') {// check that the point only befor or after number.
                if (!Character.isDigit(cellValue.charAt(i - 1)) && !Character.isDigit(cellValue.charAt(i + 1)))
                    return false;
            }
        }
        if (par != 0)//check that no way of just (.
            return false;
        return true;
    }

    public Double computeForm(String cellValue) throws Exception {
        if (cellValue == null || cellValue.isEmpty())
            throw new Exception("the formula incorrect");
        cellValue = cellValue.replaceAll(" ", "");
        double sum = -1;
        if (isNumber(cellValue))
            return Double.parseDouble(cellValue);
        if (isForm(cellValue))
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

    public static boolean isForm1(String s) {
        if (s.charAt(0) != '=')
            return false;
        if (isForm(s))
            return true;
        if (isCellform(s))
            return true;
        return false;
    }
    public static boolean isCellform(String str) {
        if (str == null || str.length() < 3) {
            return false; // The string should at least be "=A1"
        }
        if (str.charAt(0) != '=') {
            return false;
        }
            str = str.substring(1);
            if (!Character.isLetter(str.charAt(0))) // If no letters found, return false
                return false;

            int i = 1;
            // Check if the rest contains only digits
            while (i < str.length() && Character.isDigit(str.charAt(i))) {
                i++;
            }
            if (i > 2)//Check if the digits is no longer 99
                return false;
            // If there are extra characters or if the row part is missing, return false
            return i == str.length();
        }
    }
}
