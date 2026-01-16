package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;

/**
 * Instruction "return expr;"
 * @author gl10
 * @date 13/01/2026
 */
public class Return extends AbstractInst {
    private AbstractExpr exp;

    public Return(AbstractExpr exp) {
        Validate.notNull(exp);
        this.exp = exp;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType) throws ContextualError {
        
        // Verifier que nous ne sommes pas dans le Main 
        if (currentClass == null) {
             throw new ContextualError("Return can't be used in main program.", getLocation());
        }
        exp.verifyRValue(compiler, localEnv, currentClass, returnType);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        exp.decompile(s);
        s.print(";");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        exp.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        exp.prettyPrint(s, prefix, true);
    }
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        GPRegister reg = compiler.getRegisterManager().allocateRegister();
        exp.codeGenExpr(compiler, reg);
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(reg, fr.ensimag.ima.pseudocode.Register.R0));
        compiler.getRegisterManager().freeRegister(reg);
        
        fr.ensimag.ima.pseudocode.Label endLabel = compiler.getCurrentMethodEndLabel();
        if (endLabel != null) {
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BRA(endLabel));
        }
    }
}