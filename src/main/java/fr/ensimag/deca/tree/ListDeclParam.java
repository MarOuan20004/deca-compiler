package fr.ensimag.deca.tree;

import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

/**
 * List of parameter declarations.
 *
 * @author gl10
 * @date 01/01/2026
 */
public class ListDeclParam extends TreeList<AbstractDeclParam> {

    @Override
    public void decompile(IndentPrintStream s) {
        boolean first = true;
        for (AbstractDeclParam param : this.getList()) {
            if (!first) {
                s.print(", ");
            }
            param.decompile(s);
            first = false;
        }
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        for (AbstractDeclParam param : this.getList()) {
            param.prettyPrint(s, prefix, false);
        }
    }

    
}
