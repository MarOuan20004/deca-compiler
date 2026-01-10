package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

/**
 * Abstract class for method body.
 *
 * @author gl10
 * @date 01/01/2026
 */
public abstract class AbstractMethodBody extends Tree {
    
    protected void verifyMethodBody(DecacCompiler compiler, ClassDefinition currentClass,
            EnvironmentExp localEnv) throws ContextualError {
        // To be implemented by subclasses
    }
}
