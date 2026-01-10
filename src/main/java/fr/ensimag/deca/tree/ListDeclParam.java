package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
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

    /*
     * Implements non-terminal "list_decl_param" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param currentClass 
     *          corresponds to "class" attribute (null in the main bloc).
     */
    void verifyListDeclParam(DecacCompiler compiler, fr.ensimag.deca.context.EnvironmentExp localEnv)
            throws fr.ensimag.deca.context.ContextualError {
        for (AbstractDeclParam param : this.getList()) {
            param.verifyDeclParam(compiler, localEnv);
        }
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        for (AbstractDeclParam param : this.getList()) {
            param.prettyPrint(s, prefix, false);
        }
    }

    
}
