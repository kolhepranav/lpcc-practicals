import java.util.*;

public class Main {
    static class Literal {
        String literal;
        int address;

        Literal(String literal) {
            this.literal = literal;
            this.address = -1; // To be assigned after END
        }

        @Override
        public String toString() {
            return String.format("%-10s %s", literal, (address == -1 ? "Not Assigned" : address));
        }
    }

    public static void main(String[] args) {
        // Assembly code lines (input)
        String[] code = {
            "START 300",
            "READ M",
            "READ N",
            "MOVER AREG, ='51'",
            "MOVER BREG, ='61'",
            "ADD AREG, BREG",
            "LOOP MOVER CREG, M",
            "ADD CREG, ='11'",
            "COMP CREG, N",
            "BC LT, LOOP",
            "NEXT SUB AREG, ='11'",
            "COMP AREG, N",
            "BC GT, NEXT",
            "STOP",
            "M DS 1",
            "N DS 1",
            "END"
        };

        List<Literal> literalTable = new ArrayList<>();
        Map<String, Integer> literalMap = new HashMap<>();
        int lc = 0;

        // PASS 1: Scan lines and collect literals
        for (String line : code) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+|,\\s*");

            if (parts[0].equals("START")) {
                lc = Integer.parseInt(parts[1]);
                continue;
            }

            for (String part : parts) {
                if (part.startsWith("='") && part.endsWith("'")) {
                    if (!literalMap.containsKey(part)) {
                        literalMap.put(part, literalTable.size());
                        literalTable.add(new Literal(part));
                    }
                }
            }

            lc++; // One instruction per line
        }

        // PASS 2: Assign literal addresses after the last instruction
        int literalStartAddress = lc;
        for (int i = 0; i < literalTable.size(); i++) {
            literalTable.get(i).address = literalStartAddress++;
        }

        // Output Literal Table
        System.out.println("Literal Table:");
        System.out.printf("%-10s %s\n", "Literal", "Address");
        System.out.println("---------------------");
        for (Literal lit : literalTable) {
            System.out.println(lit);
        }
    }
}
