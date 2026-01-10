package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

/**
 * Abstract class for parameter declarations.
 *
 * @author gl10
 * @date 01/01/2026
 */
public abstract class AbstractDeclParam extends Tree {
    
    protected abstract void verifyDeclParam(DecacCompiler compiler, EnvironmentExp localEnv)
            throws ContextualError;
}
