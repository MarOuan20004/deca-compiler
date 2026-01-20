package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

public class GetNLines extends AbstractExpr {

    private AbstractExpr operand;

    public GetNLines(AbstractExpr operand) {
        this.operand = operand;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        operand.verifyExpr(compiler, localEnv, currentClass);
        setType(compiler.environmentType.INT);
        return compiler.environmentType.INT;
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        GPRegister rmatrix = compiler.getRegisterManager().allocateRegister();
        operand.codeGenExpr(compiler, rmatrix);
        
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.CMP(new fr.ensimag.ima.pseudocode.NullOperand(), rmatrix));
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BEQ(new fr.ensimag.ima.pseudocode.Label("dereference_null")));
        
        compiler.addInstruction(new LOAD(new RegisterOffset(0, rmatrix), r));
        compiler.getRegisterManager().freeRegister(rmatrix);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        operand.iter(f);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("getNLines(");
        operand.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        operand.prettyPrint(s, prefix, true);
    }
}
