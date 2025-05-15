import java.util.*;

public class Main {
    static class Literal {
        String literal;
        int address;
        int poolNumber;

        Literal(String literal, int poolNumber) {
            this.literal = literal;
            this.address = -1;
            this.poolNumber = poolNumber;
        }
    }

    public static void main(String[] args) {
        String[] code = {
            "START 100",
            "READ A",
            "MOVER AREG, ='1'",
            "MOVEM AREG, B",
            "MOVER BREG, ='6'",
            "ADD AREG, BREG",
            "COMP AREG, A",
            "BC GT, LAST",
            "LTORG",
            "NEXT SUB AREG, ='1'",
            "MOVER CREG, B",
            "ADD CREG, ='8'",
            "MOVEM CREG, B",
            "PRINT B",
            "LAST STOP",
            "A DS 1",
            "B DS 1",
            "END"
        };

        List<Literal> literalTable = new ArrayList<>();
        List<Integer> poolTable = new ArrayList<>();

        Map<String, Integer> literalPoolMap = new HashMap<>();
        int lc = 0; // Location counter
        int currentPool = 1;

        poolTable.add(0); // Start of first pool

        for (String line : code) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+|,\\s*");

            // Handle START
            if (parts[0].equals("START")) {
                lc = Integer.parseInt(parts[1]);
                continue;
            }

            // Handle LTORG or END
            if (parts[0].equals("LTORG") || parts[0].equals("END")) {
                // Assign addresses to unassigned literals
                for (Literal lit : literalTable) {
                    if (lit.address == -1) {
                        lit.address = lc++;
                    }
                }
                // Add new pool if more literals might come later
                if (!parts[0].equals("END") && literalTable.size() > poolTable.get(poolTable.size() - 1)) {
                    poolTable.add(literalTable.size());
                    currentPool++; // Increment pool number
                    literalPoolMap.clear(); // Clear the map for new pool
                }
                continue;
            }

            // Check for literal usage
            for (String part : parts) {
                if (part.startsWith("='") && part.endsWith("'")) {
                    String key = part;
                    // If literal is not in current pool or hasn't been seen before
                    if (!literalPoolMap.containsKey(key)) {
                        literalPoolMap.put(key, literalTable.size());
                        literalTable.add(new Literal(key, currentPool));
                    }
                }
            }

            lc++;
        }

        // Output Pool Table
        System.out.println("Pool Table:");
        System.out.printf("%-10s\n", "Pool # -> Literal Table Index");
        System.out.println("-------------------------------");
        for (int i = 0; i < poolTable.size(); i++) {
            System.out.printf("Pool %d -> %d\n", i + 1, poolTable.get(i));
        }

        // Optional: Print Literal Table for clarity
        System.out.println("\nLiteral Table:");
        System.out.printf("%-15s %-10s %s\n", "Literal", "Address", "Pool");
        System.out.println("-----------------------------------");
        for (Literal lit : literalTable) {
            System.out.printf("%-15s %-10d %d\n", lit.literal, lit.address, lit.poolNumber);
        }
    }
}
