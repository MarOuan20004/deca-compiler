package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;

/**
 * Abstract class for function declarations.
 */
public abstract class AbstractDeclFunction extends Tree {
    
    protected abstract void verifyDeclFunction(DecacCompiler compiler)
            throws ContextualError;

    protected abstract void verifyDeclFunctionBody(DecacCompiler compiler)
            throws ContextualError;

    protected abstract void codeGenDeclFunction(DecacCompiler compiler);
}
