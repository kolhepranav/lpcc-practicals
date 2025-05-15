import java.util.*;

public class Main {

    static class Symbol {
        String name;
        int address;

        Symbol(String name, int address) {
            this.name = name;
            this.address = address;
        }
    }

    static Map<String, Symbol> symbolTable = new LinkedHashMap<>();
    static List<String[]> intermediateCode = new ArrayList<>();
    static List<String> inputLines = new ArrayList<>();

    static Map<String, String> opcodeTable = new HashMap<>();
    static int locationCounter = 0;

    static void initOpcodes() {
        opcodeTable.put("READ", "01");
        opcodeTable.put("MOVER", "02");
        opcodeTable.put("SUB", "03");
        opcodeTable.put("STOP", "04");
        opcodeTable.put("START", "05");
        opcodeTable.put("END", "06");
        opcodeTable.put("DS", "07");
    }

    static void passOne() {
        for (String line : inputLines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+|,\\s*");

            if (parts[0].equals("START")) {
                locationCounter = Integer.parseInt(parts[1]);
                intermediateCode.add(new String[]{String.valueOf(locationCounter), "START", parts[1]});
                continue;
            }

            // Label handling
            if (parts.length > 1 && !opcodeTable.containsKey(parts[0])) {
                String label = parts[0];
                symbolTable.put(label, new Symbol(label, locationCounter));
                parts = Arrays.copyOfRange(parts, 1, parts.length); // drop label
            }

            // DS directive
            if (parts[0].equals("DS")) {
                intermediateCode.add(new String[]{String.valueOf(locationCounter), "DS", "1"});
                locationCounter++;
                continue;
            }

            // END directive
            if (parts[0].equals("END")) {
                intermediateCode.add(new String[]{String.valueOf(locationCounter), "END"});
                continue;
            }

            // General instructions
            String[] codeLine = new String[3];
            codeLine[0] = String.valueOf(locationCounter);
            codeLine[1] = parts[0];
            codeLine[2] = (parts.length > 2) ? parts[1] + "," + parts[2] : (parts.length > 1 ? parts[1] : "");
            intermediateCode.add(codeLine);
            locationCounter++;
        }
    }

    static void passTwo() {
        System.out.println("Intermediate Code:");
        for (String[] code : intermediateCode) {
            StringBuilder line = new StringBuilder();
            line.append(code[0]).append(" ");

            if (opcodeTable.containsKey(code[1])) {
                line.append("(").append(opcodeTable.get(code[1])).append(") ");
            } else {
                line.append(code[1]).append(" ");
            }

            if (code.length > 2 && code[2] != null && !code[2].isEmpty()) {
                String operand = code[2];
                if (operand.contains(",")) {
                    String[] ops = operand.split(",");
                    line.append(ops[0]).append(", ");
                    if (symbolTable.containsKey(ops[1])) {
                        line.append(symbolTable.get(ops[1]).address);
                    } else {
                        line.append(ops[1]);
                    }
                } else {
                    if (symbolTable.containsKey(operand)) {
                        line.append(symbolTable.get(operand).address);
                    } else {
                        line.append(operand);
                    }
                }
            }

            System.out.println(line);
        }

        System.out.println("\nSymbol Table:");
        for (Symbol symbol : symbolTable.values()) {
            System.out.println(symbol.name + " -> " + symbol.address);
        }
    }

    public static void main(String[] args) {
        initOpcodes();

        // ✅ Updated assembly input with START 300
        inputLines.add("START 300");
        inputLines.add("READ M");
        inputLines.add("READ N");
        inputLines.add("MOVER AREG, M");
        inputLines.add("SUB AREG, N");
        inputLines.add("STOP");
        inputLines.add("M DS 1");
        inputLines.add("N DS 1");
        inputLines.add("END");

        passOne();
        passTwo();
    }
}
