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
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.SUB;

/**
 *
 * @author gl10
 * @date 13/01/2026
 */
public class InstanceOf extends AbstractExpr {
    private AbstractExpr expr;
    private AbstractIdentifier typeNom;

    public InstanceOf(AbstractExpr expr, AbstractIdentifier typeNom) {
        Validate.notNull(expr);
        Validate.notNull(typeNom);
        this.expr = expr;
        this.typeNom = typeNom;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type exprType = expr.verifyExpr(compiler, localEnv, currentClass);
        Type targetType = typeNom.verifyType(compiler);
        if (!exprType.isClassOrNull()) {
            throw new ContextualError("LOperand needs to be a null or class ", expr.getLocation());
        }
        if (!targetType.isClass()) {
            throw new ContextualError("ROperand of instanceof needs to be a class ", expr.getLocation());
        }
        this.setType(compiler.environmentType.BOOLEAN);
        return compiler.environmentType.BOOLEAN;
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        int id = compiler.getLabelCounter();
        Label loopLabel = new Label("instance_of_loop_" + id);
        Label trueLabel = new Label("instance_of_true_" + id);
        Label falseLabel = new Label("instance_of_false_" + id);
        Label endLabel = new Label("instance_of_end_" + id);

        GPRegister r1 = compiler.getRegisterManager().allocateRegister();
        expr.codeGenExpr(compiler, r1);

        // Check if null
        compiler.addInstruction(new CMP(new NullOperand(), r1));
        compiler.addInstruction(new BEQ(falseLabel));

        // Get method table address of the object
        compiler.addInstruction(new LOAD(new RegisterOffset(0, r1), r1));

        compiler.addLabel(loopLabel);
        // Get method table address of the target class
        compiler.addInstruction(new LEA(typeNom.getClassDefinition().getAdrTable(), Register.R0));
        compiler.addInstruction(new CMP(Register.R0, r1));
        compiler.addInstruction(new BEQ(trueLabel));

        // Get parent method table
        compiler.addInstruction(new LOAD(new RegisterOffset(0, r1), r1));
        compiler.addInstruction(new CMP(new NullOperand(), r1));
        compiler.addInstruction(new BNE(loopLabel));

        compiler.addLabel(falseLabel);
        compiler.addInstruction(new LOAD(new ImmediateInteger(0), r));
        compiler.addInstruction(new BRA(endLabel));

        compiler.addLabel(trueLabel);
        compiler.addInstruction(new LOAD(new ImmediateInteger(1), r));

        compiler.addLabel(endLabel);
        compiler.getRegisterManager().freeRegister(r1);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        expr.decompile(s);
        s.print(" instanceof ");
        typeNom.decompile(s);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        typeNom.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        typeNom.prettyPrint(s, prefix, true);
    }
}