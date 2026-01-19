package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;

/**
 * Abstract class for method body.
 *
 * @author gl10
 * @date 01/01/2026
 */
public abstract class AbstractMethodBody extends Tree {
    
    protected abstract void verifyMethodBody(DecacCompiler compiler,EnvironmentExp localEnv,ClassDefinition currentClass,Type returnType) throws ContextualError;

    protected void codeGenMethodBody(DecacCompiler compiler){
        codeGenMethodBody(compiler, 0);
    }
    protected abstract void codeGenMethodBody(DecacCompiler compiler, int nbSavedRegisters);
    
    protected abstract void codeGenMethodEnd(DecacCompiler compiler);
}
