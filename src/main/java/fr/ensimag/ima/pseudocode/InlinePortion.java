package fr.ensimag.ima.pseudocode;

import java.io.PrintStream;

/**
 * Portion of IMA assembly code to be dumped verbatim into the
 * generated code.
 *
 * @author Ensimag
 * @date 01/01/2026
 */
public class InlinePortion extends AbstractLine {
    private final String asmCode;
    
    public InlinePortion(String asmCode) {
        super();
        this.asmCode = asmCode;
    }
    
    @Override
    void display(PrintStream s) {
        for (String line : asmCode.split("\n")) {
            if (line.trim().isEmpty()) {
                s.println();
            } else if (line.startsWith(" ") || line.startsWith("\t")) {
                s.println(line);
            } else {
                s.println("\t" + line);
            }
        }
    }

}
