import java.util.*;

public class Main {
    static class Symbol {
        String name;
        int address;

        Symbol(String name, int address) {
            this.name = name;
            this.address = address;
        }

        @Override
        public String toString() {
            return String.format("%-10s %d", name, address);
        }
    }

    public static void main(String[] args) {
        // Updated Assembly code input
        String[] code = {
            "START 180",
            "READ M",
            "READ N",
            "LOOP MOVER AREG, M",
            "     MOVER BREG, N",
            "     COMP BREG, ='200'",
            "     BC GT, LOOP",
            "BACK SUB AREG, M",
            "     COMP AREG, ='500'",
            "     BC LT, BACK",
            "     STOP",
            "M DS 1",
            "N DS 1",
            "END"
        };

        Map<String, Symbol> symbolTable = new LinkedHashMap<>();
        int lc = 0; // Location counter

        for (String line : code) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+|,\\s*");

            // Handle START directive
            if (parts[0].equals("START")) {
                lc = Integer.parseInt(parts[1]);
                continue;
            }

            // Handle labeled instructions or variables
            if (!isOpcode(parts[0])) {
                String label = parts[0];

                if (parts.length > 1 && parts[1].equals("DS")) {
                    // Define storage
                    symbolTable.put(label, new Symbol(label, lc));
                    lc += Integer.parseInt(parts[2]);
                } else {
                    // Label for instruction
                    symbolTable.put(label, new Symbol(label, lc));
                    lc++;
                }
            } else {
                // Normal instruction
                lc++;
            }
        }

        // Print Symbol Table
        System.out.println("Symbol Table:");
        System.out.printf("%-10s %s\n", "Symbol", "Address");
        System.out.println("---------------------");

        for (Symbol sym : symbolTable.values()) {
            System.out.println(sym);
        }
    }

    // Check if token is an opcode or assembler directive
    private static boolean isOpcode(String token) {
        String[] opcodes = {
            "START", "READ", "MOVER", "MOVEM", "ADD", "SUB",
            "COMP", "BC", "STOP", "END"
        };
        for (String op : opcodes) {
            if (token.equals(op)) return true;
        }
        return false;
    }
}
