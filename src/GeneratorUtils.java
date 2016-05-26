/**
 * @author Alexey Katsman
 * @since 25.05.16
 */

import java.io.*;
import java.util.*;

@SuppressWarnings("unused")
public class GeneratorUtils {
    private static HashSet<StringBuilder> was = new HashSet<>();

    public static StringBuilder generate(int len) {
        Random random = new Random();
        HashSet<StringBuilder> was = new HashSet<>();
        was.addAll(GeneratorUtils.was);

        do {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < len; i++) {
                int curr = random.nextInt(400) % 4;

                if (i == 0 && curr < 2) {
                    curr += 2;
                }

                char appendChar;

                switch (curr) {
                    case 0:
                        appendChar = '0';
                        break;
                    case 1:
                        appendChar = '1';
                        break;
                    case 2:
                        appendChar = 'O';
                        break;
                    case 3:
                        appendChar = 'I';
                        break;
                    default:
                        appendChar = '_';
                }

                sb.append(appendChar);
            }

            if (GeneratorUtils.was.contains(sb)) {
                was.remove(sb);
                continue;
            }

            GeneratorUtils.was.add(sb);
            return sb;
        } while (!was.isEmpty());

        ObfuscatorListener.currentLength++;
        return generate(len + 1);
    }

    public static StringBuilder multiLineString(String src, int level) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < src.length() - 1; i++) {
            sb.append(src.charAt(i));
            sb.append("\" + \n");

            for (int j = 0; j < level + 2; j++) {
                sb.append("\t");
            }

            sb.append("\"");
        }

        sb.append(src.charAt(src.length() - 1));
        return sb;
    }

    public static String getBooleanValue() {
        Random random = new Random();
        int num = random.nextInt(1000) % 2;

        if (num == 0) {
            return "false";
        } else {
            return "true";
        }
    }

    public static String getCharValue() {
        Random random = new Random();
        int num = random.nextInt(1000) % 250;
        num += 5;
        char c = (char) num;
        return ("'" + c + "'");
    }

    public static String getByteValue() {
        Random random = new Random();
        byte res = (byte) (random.nextInt(100000) - 50000);
        return String.valueOf(res);
    }

    public static String getShortValue() {
        Random random = new Random();
        short res = (short) (random.nextInt(10000) - 5000);
        return String.valueOf(res);
    }

    public static String getIntValue() {
        Random random = new Random();
        int res = random.nextInt();
        return String.valueOf(res);
    }

    public static String getLongValue() {
        Random random = new Random();
        long res = random.nextLong();
        return String.valueOf(res) + "L";
    }

    public static String getFloatValue() {
        Random random = new Random();
        float res = random.nextFloat();
        return String.valueOf(res);
    }

    public static String getDoubleValue() {
        Random random = new Random();
        double res = random.nextDouble();
        return String.valueOf(res);
    }
}
