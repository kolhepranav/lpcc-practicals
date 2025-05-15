import java.util.*;

public class Main {

    static class MNTEntry {
        String macroName;
        int mdtIndex;
        List<String> formalParams;  // List to store formal parameters

        MNTEntry(String name, int index) {
            macroName = name;
            mdtIndex = index;
            formalParams = new ArrayList<>();
        }
    }

    static class MacroCall {
        String macroName;
        List<String> actualParams;
        int callNumber; // To track call instances (e.g., XYZ-1, XYZ-2)

        MacroCall(String name, List<String> params, int num) {
            macroName = name;
            actualParams = new ArrayList<>(params);
            callNumber = num;
        }
    }

    static List<MNTEntry> MNT = new ArrayList<>();
    static List<String> MDT = new ArrayList<>();
    static Map<String, Integer> MNTMap = new HashMap<>();
    static Map<String, List<String>> actualParamMap = new HashMap<>();  // To store actual parameters for each macro call
    static List<MacroCall> macroCalls = new ArrayList<>();
    static Map<String, Integer> macroCallCounters = new HashMap<>(); // To track number of calls for each macro

    static List<String> inputProgram = new ArrayList<>();
    static List<String> intermediateCode = new ArrayList<>();

    static void loadInputProgram() {
        // Static input as per your example
//         inputProgram.add("LOAD J");
//         inputProgram.add("LOAD J
// STORE M");
//         inputProgram.add("MACRO EST");
//         inputProgram.add("LOAD e");
//         inputProgram.add("ADD d");

//         inputProgram.add("MEND");

//         inputProgram.add("MULT D");
//         inputProgram.add("MACRO ADD1 ARG");
//         inputProgram.add("LOAD X");
//         inputProgram.add("STORE ARG");
//         inputProgram.add("MEND");
//         inputProgram.add("LOAD B");
//         inputProgram.add("MACRO ADD5 A1, A2, A3");
//         inputProgram.add("STORE A2");
//         inputProgram.add("ADD1 5");
//         inputProgram.add("ADD1 10");
//         inputProgram.add("LOAD A1");
//         inputProgram.add("LOAD A3");
//         inputProgram.add("MEND");
//         inputProgram.add("ADD1 t");
//         inputProgram.add("ABC");
//         inputProgram.add("ADD5 D1, D2, D3");
//         inputProgram.add("END");





  
inputProgram.add("LOAD J");
inputProgram.add("STORE M");
inputProgram.add("MACRO EST");
inputProgram.add("LOAD e");
inputProgram.add("ADD d");
inputProgram.add("MEND");
inputProgram.add("LOAD S");
inputProgram.add("MACRO SUB4 ABC");
inputProgram.add("LOAD U");
inputProgram.add("STORE ABC");
inputProgram.add("MEND");
inputProgram.add("LOAD P");
inputProgram.add("ADD V");
inputProgram.add("MACRO ADD7 P4, P5, P6");
inputProgram.add("LOAD P5");
inputProgram.add("SUB4 XYZ");
inputProgram.add("SUB 8");
inputProgram.add("SUB 2");
inputProgram.add("STORE P4");
inputProgram.add("STORE P6");
inputProgram.add("MEND");
inputProgram.add("EST ");
inputProgram.add("ADD7 C4, C5, C6");
inputProgram.add("SUB4 z");
inputProgram.add("END");








    }

    static void passOne() {
        boolean inMacroDef = false;
        List<String> currentMacroBody = new ArrayList<>();
        String currentMacroName = "";
        int mdtIndex = 0;
        List<String> currentFormalParams = new ArrayList<>();

        for (int i = 0; i < inputProgram.size(); i++) {
            String line = inputProgram.get(i).trim();

            if (line.startsWith("MACRO")) {
                inMacroDef = true;
                currentMacroBody.clear();
                currentFormalParams.clear();
                
                String[] tokens = line.split("\\s+|,\\s*");
                currentMacroName = tokens[1];
                mdtIndex = MDT.size();
                
                MNTEntry entry = new MNTEntry(currentMacroName, mdtIndex);
                
                // Extract formal parameters
                for (int j = 2; j < tokens.length; j++) {
                    String param = tokens[j].trim();
                    if (!param.isEmpty()) {
                        entry.formalParams.add(param);
                        currentFormalParams.add(param);
                    }
                }
                
                MNT.add(entry);
                MNTMap.put(currentMacroName, mdtIndex);
                
                // Don't add MACRO lines to MDT
            } else if (line.equals("MEND")) {
                inMacroDef = false;
                MDT.add("MEND");
            } else if (inMacroDef) {
                // Replace formal parameters with positional markers (#1, #2, etc.)
                String modifiedLine = line;
                for (int j = 0; j < currentFormalParams.size(); j++) {
                    String param = currentFormalParams.get(j);
                    modifiedLine = modifiedLine.replaceAll("\\b" + param + "\\b", "#" + (j+1));
                }
                MDT.add(modifiedLine);
            } else {
                // Check if this is a macro call and store actual parameters
                String[] tokens = line.split("\\s+|,\\s*");
                String opcode = tokens[0];
                
                if (MNTMap.containsKey(opcode)) {
                    List<String> actualParams = new ArrayList<>();
                    for (int j = 1; j < tokens.length; j++) {
                        String param = tokens[j].trim();
                        if (!param.isEmpty()) {
                            actualParams.add(param);
                        }
                    }
                    
                    // Update call counter for this macro
                    int callNumber = macroCallCounters.getOrDefault(opcode, 0) + 1;
                    macroCallCounters.put(opcode, callNumber);
                    
                    // Store complete call information
                    macroCalls.add(new MacroCall(opcode, actualParams, callNumber));
                    
                    // Also store in the old structure for backward compatibility
                    actualParamMap.put(opcode + "_" + i, actualParams);
                }
                
                intermediateCode.add(line); // non-macro lines go directly
            }
        }
    }

    static void passTwo() {
        System.out.println("\n=== Intermediate Code After Macro Expansion ===\n");

        for (String line : intermediateCode) {
            String[] tokens = line.split("\\s+|,\\s*");
            String opcode = tokens[0];

            if (MNTMap.containsKey(opcode)) {
                int mdtIndex = MNTMap.get(opcode);
                List<String> actualParams = new ArrayList<>();
                
                // Collect actual parameters from this macro call
                for (int i = 1; i < tokens.length; i++) {
                    if (!tokens[i].trim().isEmpty()) {
                        actualParams.add(tokens[i]);
                    }
                }
                
                // expand macro without showing parameter mapping
                int idx = mdtIndex;
                while (idx < MDT.size() && !MDT.get(idx).equals("MEND")) {
                    String expLine = MDT.get(idx);
                    
                    // Replace positional parameters (#1, #2, etc.) with actual values
                    for (int i = 0; i < actualParams.size(); i++) {
                        expLine = expLine.replaceAll("#" + (i+1), actualParams.get(i));
                    }
                    
                    System.out.println(expLine);  // Print directly without extra information
                    idx++;
                }
            } else {
                System.out.println(line);
            }
        }
    }

    static void displayMDT() {
        System.out.println("\n=== Macro Definition Table (MDT) ===");
        System.out.println("Index   Instruction");
        System.out.println("-----   -----------");
        for (int i = 0; i < MDT.size(); i++) {
            System.out.printf("%-7d %s\n", i, MDT.get(i));
        }
    }
    
    static void displayMNT() {
        System.out.println("\n=== Macro Name Table (MNT) ===");
        System.out.printf("%-10s %-15s %-10s\n", "Name", "No. of params", "MDT Index");
        System.out.printf("%-10s %-15s %-10s\n", "----", "-------------", "---------");
        
        for (MNTEntry entry : MNT) {
            System.out.printf("%-10s %-15d %-10d\n", 
                entry.macroName, 
                entry.formalParams.size(),
                entry.mdtIndex);
        }
    }

    static void displayParameterTablesFormatted() {
        for (MacroCall call : macroCalls) {
            String macroName = call.macroName;
            List<String> actualParams = call.actualParams;
            int callNumber = call.callNumber;
            
            // Find corresponding MNT entry for formal parameters
            MNTEntry mntEntry = null;
            for (MNTEntry entry : MNT) {
                if (entry.macroName.equals(macroName)) {
                    mntEntry = entry;
                    break;
                }
            }
            
            if (mntEntry == null) continue;
            List<String> formalParams = mntEntry.formalParams;
            
            System.out.println("\nFor macro " + macroName + ":");
            System.out.println("Formal v/s positional parameter list and Actual");
            System.out.println("v/s positional parameter list (" + macroName + "-" + callNumber + ")");
            System.out.println();
            
            // Print formal parameters table
            System.out.printf("%-12s %-12s\n", "Formal", "Positional");
            System.out.printf("%-12s %-12s\n", "parameter", "parameter");
            
            for (int i = 0; i < formalParams.size(); i++) {
                System.out.printf("%-12s %-12s\n", formalParams.get(i), "#" + (i+1));
            }
            
            System.out.println();
            
            // Print actual parameters table
            System.out.printf("%-12s %-12s\n", "Actual", "Positional");
            System.out.printf("%-12s %-12s\n", "parameter", "parameter");
            
            for (int i = 0; i < actualParams.size(); i++) {
                // Ensure we don't exceed formal params length
                if (i < formalParams.size()) {
                    System.out.printf("%-12s %-12s\n", actualParams.get(i), "#" + (i+1));
                }
            }
            
            System.out.println("\n");
        }
    }

    public static void main(String[] args) {
        loadInputProgram();
        passOne();

        // Print intermediate code before expansion
        System.out.println("=== Intermediate Code (Before Expansion) ===\n");
        for (String line : intermediateCode) {
            System.out.println(line);
        }

        // Display MNT and MDT in the requested format
        displayMNT();
        displayMDT();
        
        passTwo();
        
        // Display parameter tables in the requested format
        System.out.println("\n=== Parameter Tables ===");
        displayParameterTablesFormatted();
    }
}
