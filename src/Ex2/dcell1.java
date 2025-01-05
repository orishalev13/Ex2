package Ex2;

import java.util.Stack;

public class dcell1 {
    public String cellValue  ;
    public dcell1(String cellValue){
        this.cellValue=cellValue;
    }
    public String GetcellValue() {
        return this.cellValue;
    }
    public void SetcellValue(String cellValue) {
        this.cellValue = cellValue;
    }

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
    public boolean isText(String cellValue){
        cellValue = cellValue.replaceAll(" ","");
        if (cellValue == null || cellValue.isEmpty()) {
            return false; // Return false for null or empty strings
        }
        if (cellValue.charAt(0)=='=')
            return false;
        if (isForm(cellValue))
            return false;
        if (isNumber(cellValue))
            return false;
        return true;


    }
    static boolean isForm(String cellValue) {
        cellValue = cellValue.replaceAll(" ", "");
        if (cellValue == null || cellValue.isEmpty()) {
            return false; // Return false for null or empty strings
        }
        if (cellValue.charAt(0) != '=') {
            return false; //   only '=' at the start
        }
        int par=0;boolean s=false;
        for (int i = 1; i < cellValue.length(); i++) {
            if (!Character.isDigit(cellValue.charAt(i)) && !("()+-*/.".contains(cellValue.charAt(i) + "")))// there only char from type numbers or -=\*.()
                return false;
            if(cellValue.charAt(i)=='('&& cellValue.charAt(i+1)==')')// there no way of  empty ().
                return false;

            if ("+-*/".contains(cellValue.charAt(i) + "")) {// check if there ++,--,//,**
                if (s)
                    return false;
                s = true;
            }
            if("+-".contains(cellValue.charAt(i) + "")  ) { // if there -+ check
                if (i < cellValue.length() - 1 && cellValue.charAt(i + 1) == ')')
                    return false;//check if there -) that not correct

                // Check if the character is surrounded correctly
                if (i > 0 && (Character.isDigit(cellValue.charAt(i - 1)) || "()".contains(cellValue.charAt(i-1)+""))) {
                    continue;
                }
                if (i < cellValue.length() - 1 && (Character.isDigit(cellValue.charAt(i + 1)) || cellValue.charAt(i + 1) == '(')) {
                    continue;
                }
                return false;
            }
            if("/*".contains(cellValue.charAt(i) + "")  )
            {
                if (i > 0 && (Character.isDigit(cellValue.charAt(i - 1)) || "()".contains(cellValue.charAt(i-1)+""))) {
                    continue;
                }
                if (i < cellValue.length() - 1 && (Character.isDigit(cellValue.charAt(i + 1)) || cellValue.charAt(i + 1) == '(')) {
                    continue;
                }
                return false;
            }
            {
                if((!"()".contains(cellValue.charAt(i-1)+"") && !Character.isDigit(cellValue.charAt(i-1)))||(!(cellValue.charAt(i+1)=='(') && !Character.isDigit(cellValue.charAt(i-1))))
                    return false;

            }
            if (cellValue.charAt(i) == '.') {// check that the point only befor or after number.
                if (!Character.isDigit(cellValue.charAt(i - 1)) && !Character.isDigit(cellValue.charAt(i + 1)))
                    return false;
            }

            if (cellValue.charAt(i) == '(') //check that no way of )( or just).
                par++;
            if (cellValue.charAt(i) == ')')
                par--;
            if (par < 0)
                return false;

        }
        if (par!=0)//check that no way of just (.
            return false;
        return true;
    }
    public  Double computeForm(String cellValue) {
        if (!isForm(cellValue))
            return (double) -1;
        return 1.0;

    }// “=1+2*2” → 5,// “=((1+2)*2)-1”→ 5
       public int indoflast() {
     int i=0;
    return i;

    }
    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
    private static int precedence(char operator) {
        return switch (operator) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> 0;
        };
    }
  private static double applyOperator(double a, double b, char operator) throws Exception {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> a / b;
            default -> throw new Exception("the operatror incorrect");
        };}

    double evaluateExpression(String postfix) throws Exception {
        Stack<Double> stack = new Stack<>();
        String[] tokens = postfix.split(" "); // מפצל את הביטוי לפורמט postfix

        for (String token : tokens) {
            if (isNumber(token)) {
                // אם זה מספר, דוחף אותו למחסנית
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token.charAt(0))) {
                // אם זה אופרטור, מבצע חישוב עם שני המספרים האחרונים במחסנית
                if (stack.size() < 2) {
                    throw new Exception("ביטוי לא תקין");
                }
                double b = stack.pop();
                double a = stack.pop();
                stack.push(applyOperator(a, b, token.charAt(0)));
            }
        }


        // בסיום, אמור להישאר מספר יחיד במחסנית - התוצאה
        if (stack.size() != 1) {
            throw new Exception("ביטוי לא תקין");
        }

        return stack.pop();
    }
}
