package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;

/**
 * Abstract class for field declarations.
 *
 * @author gl10
 * @date 01/01/2026
 */
public abstract class AbstractDeclField extends Tree {
    
    protected abstract void verifyDeclField(DecacCompiler compiler, ClassDefinition currentClass)
            throws ContextualError ;
}
