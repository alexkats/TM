/**
 * @author Alexey Katsman
 * @since 26.05.16
 */

import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class ObfuscatorListener extends JavaBaseListener {
    private ArrayList<HashMap<String, String>> varMapperList = new ArrayList<>();
    private int bracesCount = 0;
    private boolean wasNewLine = false;
    private boolean isVisited = false;
    public static int currentLength = 6;

    private void levelInc() {
        varMapperList.add(new HashMap<>());
        bracesCount++;
    }

    private void levelDec() {
        varMapperList.remove(bracesCount - 1);
        bracesCount--;
    }

    @Override
    public void enterClassBody(JavaParser.ClassBodyContext ctx) {
        super.enterClassBody(ctx);
        levelInc();
    }

    @Override
    public void exitClassBody(JavaParser.ClassBodyContext ctx) {
        levelDec();
        super.exitClassBody(ctx);
    }

    @Override
    public void enterEnumBody(JavaParser.EnumBodyContext ctx) {
        super.enterEnumBody(ctx);
        levelInc();
    }

    @Override
    public void exitEnumBody(JavaParser.EnumBodyContext ctx) {
        levelDec();
        super.exitEnumBody(ctx);
    }

    @Override
    public void enterInterfaceBody(JavaParser.InterfaceBodyContext ctx) {
        super.enterInterfaceBody(ctx);
        levelInc();
    }

    @Override
    public void exitInterfaceBody(JavaParser.InterfaceBodyContext ctx) {
        levelDec();
        super.exitInterfaceBody(ctx);
    }

    @Override
    public void enterCodeBlock(JavaParser.CodeBlockContext ctx) {
        super.enterCodeBlock(ctx);
        levelInc();
    }

    @Override
    public void exitCodeBlock(JavaParser.CodeBlockContext ctx) {
        levelDec();
        super.exitCodeBlock(ctx);
    }

    @Override
    public void enterMainExpression(JavaParser.MainExpressionContext ctx) {
        super.enterMainExpression(ctx);

        if (ctx.ValidId() == null) {
            return;
        }

        isVisited = true;

        String var = ctx.ValidId().getText();
        String value = null;

        for (int i = bracesCount - 1; i >= 0; i--) {
            if (varMapperList.get(i).containsKey(var)) {
                value = varMapperList.get(i).get(var);
                break;
            }
        }

        if (value != null) {
            checkAndPrintTabs();
            System.out.print(value + " ");
        }
    }

    @Override
    public void enterVarId(JavaParser.VarIdContext ctx) {
        super.enterVarId(ctx);
        isVisited = true;
        String var = ctx.ValidId().getText();
        StringBuilder generated = GeneratorUtils.generate(currentLength);
        checkAndPrintTabs();
        varMapperList.get(bracesCount - 1).put(var, generated.toString());
        System.out.print(generated + " ");
    }

    @Override
    public void enterValue(JavaParser.ValueContext ctx) {
        super.enterValue(ctx);

        try {
            System.out.print(GeneratorUtils.multiLineString(ctx.StringValue().getText(), bracesCount));
            isVisited = true;
        } catch (NullPointerException ignored) {

        }
    }

    private void checkAndPrintTabs() {
        if (wasNewLine) {
            for (int i = 0; i < bracesCount; i++) {
                System.out.print("\t");
            }

            wasNewLine = false;
        }
    }

    private void maybeObfuscate() {
        Random random = new Random();
        int probability = random.nextInt(1000) % 5;

        if (probability != 0) {
            return;
        }

        obfuscate();
    }

    private void obfuscate() {
        String type = getRandomType();
        String name = GeneratorUtils.generate(currentLength).toString();
        checkAndPrintTabs();
        System.out.println(type + " " + name + " = " + getValue(type) + ";");
        wasNewLine = true;
        checkAndPrintTabs();
        System.out.print("{");
        System.out.print(name + " = " + getValue(type) + ";");
        System.out.println("}");
        wasNewLine = true;
    }

    private String getRandomType() {
        Random random = new Random();
        int num = random.nextInt(1000) % 8;

        switch (num) {
            case 0:
                return "boolean";
            case 1:
                return "char";
            case 2:
                return "byte";
            case 3:
                return "short";
            case 4:
                return "int";
            case 5:
                return "long";
            case 6:
                return "float";
            case 7:
                return "double";
            default:
                return "";
        }
    }

    private String getValue(String type) {
        switch (type) {
            case "boolean":
                return GeneratorUtils.getBooleanValue();
            case "char":
                return GeneratorUtils.getCharValue();
            case "byte":
                return GeneratorUtils.getByteValue();
            case "short":
                return GeneratorUtils.getShortValue();
            case "int":
                return GeneratorUtils.getIntValue();
            case "long":
                return GeneratorUtils.getLongValue();
            case "float":
                return GeneratorUtils.getFloatValue();
            case "double":
                return GeneratorUtils.getDoubleValue();
            default:
                return "";
        }
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        super.visitTerminal(node);

        if (isVisited) {
            if (Character.isUpperCase(node.getText().charAt(0))) {
                checkAndPrintTabs();
                System.out.print(node.getText() + " ");
            }

            isVisited = false;
            return;
        }

        if (node.getText().equals(";") || node.getText().equals("{") || node.getText().equals("}")) {
            if (wasNewLine) {
                for (int i = 0; i < bracesCount - 1; i++) {
                    System.out.print("\t");
                }
            }

            System.out.println(node.getText());
            wasNewLine = true;

            if (node.getParent() instanceof JavaParser.ExpressionInParenContext) {
                return;
            }

            if (node.getText().equals("}")) {
                bracesCount--;

                if (bracesCount > 0) {
                    maybeObfuscate();
                }

                bracesCount++;
            } else {
                if (bracesCount > 0) {
                    maybeObfuscate();
                }
            }
        } else {
            checkAndPrintTabs();
            System.out.print(node.getText() + " ");
        }
    }
}
