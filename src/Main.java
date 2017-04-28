import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        String mystr = "(1+(1+(1+(1+(1+(1+1)*2)*3)/4)*5)+6)";
        try {
            System.out.println(parseLine(mystr));

            // validate answer
            int result = 0;
            ScriptEngineManager mgr = new ScriptEngineManager();
            ScriptEngine engine = mgr.getEngineByName("JavaScript");
            System.out.println((Number)engine.eval(mystr));

        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    private static double calculator (String a, String op, String b) {
        double da, db;
        try {
            da = Double.parseDouble(a);
        } catch (Exception e) {
            da = parseLine(a);
        }

        try {
            db = Double.parseDouble(b);
        } catch (Exception e) {
            db = parseLine(b);
        }

        switch(op) {
            case "*":
                return da * db;
            case "/":
                return da / db;
            case "+":
                return da + db;
            case "-":
                return da - db;
        }

        return 0.0;
    }


    private static double parseLine (String line) {
        if (countCharOccurrence(line, '(') != countCharOccurrence(line, ')')) {
            try {
                throw new Exception("invalid syntax");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        Matcher match = Pattern.compile("\\(.*\\)|\\d+").matcher(line);
        Pattern operatorsPattern = Pattern.compile("\\+|-|\\*|/");

        ArrayList<String> oprands = new ArrayList<>();
        ArrayList<String> operators = new ArrayList<>();
        int lastOprandEnd = 0;
        while(match.find()) {
            if (lastOprandEnd > 0) {
                String subString = line.substring(lastOprandEnd, match.start());
                Matcher operatorMatcher = operatorsPattern.matcher(subString);
                if (operatorMatcher.find()) {
                    operators.add(operatorMatcher.group());
                }
            }
            oprands.add(match.group());
            lastOprandEnd = match.end();
        }

        if (oprands.size() == 1) {
            String oprand = oprands.get(0);
            Matcher pMatcher = Pattern.compile("\\((.*)\\)").matcher(oprand);
            if (pMatcher.find()) {
                return parseLine(pMatcher.group(1));
            }
            return Double.parseDouble(oprands.get(0));
        }

        int operatorIndex = 0;
        int pmIndex = operators.indexOf("+");
        if (pmIndex > 0) {
            operatorIndex = pmIndex;
        }
        pmIndex = operators.indexOf("-");
        if (pmIndex > 0) {
            if (operatorIndex > 0) {
                operatorIndex = Math.min(operatorIndex, pmIndex);
            } else {
                operatorIndex = pmIndex;
            }
        }

        String operator = operators.get(operatorIndex);
        StringBuilder op1SB = new StringBuilder();
        String op1 = "";
        for (int i = 0; i < operatorIndex; i++) {
            op1SB.append(oprands.get(i)).append(operators.get(i));
        }
        op1SB.append(oprands.get(operatorIndex));
        op1 = op1SB.toString();

        StringBuilder op2SB = new StringBuilder();
        String op2 = "";
        for (int i = operatorIndex + 1, length = operators.size(); i < length; i++) {
            op2SB.append(oprands.get(i)).append(operators.get(i));
        }
        op2SB.append(oprands.get(oprands.size() - 1));
        op2 = op2SB.toString();

        return calculator(op1, operator, op2);
    }

    private static int countCharOccurrence(String string, char c) {
        int count = 0;
        for (int i = 0, length = string.length(); i < length; i++) {
            if (string.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }
}
