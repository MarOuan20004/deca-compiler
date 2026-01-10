package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

/**
 * Abstract class for method declarations.
 *
 * @author gl10
 * @date 01/01/2026
 */
public abstract class AbstractDeclMethod extends Tree {
    
    protected abstract void verifyDeclMethod(DecacCompiler compiler, ClassDefinition currentClass)
            throws ContextualError;
}
