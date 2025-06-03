import java.util.*;

public class ExpressionEvaluator {
    
    /**
     * Вычисление математического выражения
     */
    public static double evaluate(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be empty");
        }
        
        // Убираем пробелы
        expression = expression.replaceAll("\\s+", "");
        
        // Преобразуем в постфиксную нотацию и вычисляем
        List<String> postfix = convertToPostfix(expression);
        return evaluatePostfix(postfix);
    }
    
    /**
     * Преобразование в постфиксную нотацию (алгоритм Dijkstra)
     */
    private static List<String> convertToPostfix(String expression) {
        List<String> output = new ArrayList<>();
        Stack<String> operators = new Stack<>();
        
        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);
            
            // Если число (включая отрицательные и десятичные)
            if (Character.isDigit(c) || (c == '-' && isStartOfNumber(expression, i))) {
                StringBuilder number = new StringBuilder();
                
                // Обработка отрицательного числа
                if (c == '-') {
                    number.append(c);
                    i++;
                    c = expression.charAt(i);
                }
                
                // Считываем число
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    number.append(expression.charAt(i));
                    i++;
                }
                output.add(number.toString());
                continue;
            }
            
            // Открывающая скобка
            if (c == '(') {
                operators.push(String.valueOf(c));
            }
            // Закрывающая скобка
            else if (c == ')') {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                if (!operators.isEmpty()) {
                    operators.pop(); // Убираем "("
                }
            }
            // Оператор
            else if (isOperator(c)) {
                String operator = String.valueOf(c);
                while (!operators.isEmpty() && !operators.peek().equals("(") &&
                       getPrecedence(operators.peek()) >= getPrecedence(operator)) {
                    output.add(operators.pop());
                }
                operators.push(operator);
            }
            
            i++;
        }
        
        // Добавляем оставшиеся операторы
        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }
        
        return output;
    }
    
    /**
     * Вычисление постфиксного выражения
     */
    private static double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();
        
        for (String token : postfix) {
            if (isOperator(token.charAt(0)) && token.length() == 1) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Incorrect expression");
                }
                
                double b = stack.pop();
                double a = stack.pop();
                double result = performOperation(a, b, token.charAt(0));
                stack.push(result);
            } else {
                try {
                    stack.push(Double.parseDouble(token));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Inappropriate number: " + token);
                }
            }
        }
        
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Incorrect expression");
        }
        
        return stack.pop();
    }
    
    /**
     * Выполнение арифметической операции
     */
    private static double performOperation(double a, double b, char operator) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("Division into zero!");
                }
                return a / b;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
    
    /**
     * Проверка, является ли символ оператором
     */
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }
    
    /**
     * Получение приоритета оператора
     */
    private static int getPrecedence(String operator) {
        switch (operator) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            default:
                return 0;
        }
    }
    
    /**
     * Проверка, является ли минус началом отрицательного числа
     */
    private static boolean isStartOfNumber(String expression, int index) {
        if (index == 0) return true;
        
        char prevChar = expression.charAt(index - 1);
        return prevChar == '(' || isOperator(prevChar);
    }
    
    /**
     * Демонстрация работы класса
     */
    public static void main(String[] args) {
        System.out.println("=== Calculator tests ===");
        
        String[] tests = {
            "2 + 3 * 4",
            "(10 + 5) / 3",
            "2 * (3 + 4) - 1",
            "-5 + 3",
            "10 / 2 + 3 * 4",
            "(2 + 3) * (4 - 1)"
        };
        
        for (String test : tests) {
            try {
                double result = evaluate(test);
                System.out.println(test + " = " + result);
            } catch (Exception e) {
                System.out.println(test + " = Error: " + e.getMessage());
            }
        }
    }
}