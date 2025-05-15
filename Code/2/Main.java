import java.util.*;

public class Main {
    static class Literal {
        String literal;
        int address;

        Literal(String literal) {
            this.literal = literal;
            this.address = -1; // Address assigned in Pass 2
        }

        @Override
        public String toString() {
            return String.format("%-10s %s", literal, (address == -1 ? "Not Assigned" : address));
        }
    }

    public static void main(String[] args) {
        // Updated Assembly code input
        String[] code = {
            "START 100",
            "READ A",
            "READ B",
            "MOVER AREG, ='50'",
            "MOVER BREG, ='60'",
            "ADD AREG, BREG",
            "LOOP MOVER CREG, A",
            "ADD CREG, ='10'",
            "COMP CREG, B",
            "BC LT, LOOP",
            "NEXT SUB AREG, ='10'",
            "COMP AREG, B",
            "BC GT, NEXT",
            "STOP",
            "A DS 1",
            "B DS 1",
            "END"
        };

        List<Literal> literalTable = new ArrayList<>();
        Map<String, Integer> literalMap = new HashMap<>();
        int lc = 0; // Location counter

        // PASS 1: Collect literals
        for (String line : code) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+|,\\s*");

            if (parts[0].equals("START")) {
                lc = Integer.parseInt(parts[1]);
                continue;
            }

            // Check for literal usage
            for (String part : parts) {
                if (part.startsWith("='") && part.endsWith("'")) {
                    if (!literalMap.containsKey(part)) {
                        literalMap.put(part, literalTable.size());
                        literalTable.add(new Literal(part));
                    }
                }
            }

            // Assume each instruction takes 1 memory word
            lc++;
        }

        // PASS 2: Assign addresses to literals (after END or LTORG)
        int literalStartAddress = lc;
        for (int i = 0; i < literalTable.size(); i++) {
            literalTable.get(i).address = literalStartAddress++;
        }

        // Print Literal Table
        System.out.println("Literal Table:");
        System.out.printf("%-10s %s\n", "Literal", "Address");
        System.out.println("---------------------");

        for (Literal lit : literalTable) {
            System.out.println(lit);
        }
    }
}
