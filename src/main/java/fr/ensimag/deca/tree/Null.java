package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import java.io.PrintStream;

/**
 * "null" keyword
 *
 * @author gl10
 * @date 13/01/2026
 */
public class Null extends AbstractExpr {

    @Override
public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
        ClassDefinition currentClass) throws ContextualError {
    Type nulType = compiler.environmentType.defOfType(compiler.symbolTable.create("null")).getType();
    this.setType(nulType);
    return nulType;
}

   

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(new fr.ensimag.ima.pseudocode.NullOperand(), r));
    }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("null");
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
