package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

/**
 * "this" keyword
 *
 * @author gl10
 * @date 01/01/2026
 */
public class This extends AbstractExpr {
    
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
        ClassDefinition currentClass) throws ContextualError {
    if (currentClass == null) {
        throw new ContextualError("this can't be used in main progrm",getLocation());
    }
    Type thisType = currentClass.getType();
    this.setType(thisType);
    return thisType;
}

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("this");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // No children
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // No children
    }
}
