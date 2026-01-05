package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl10
 * @date 01/01/2026
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type left = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type right = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if (left.isInt() && right.isInt()) {
            setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }
        if (left.isFloat() && right.isFloat()) {
            setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }
        if (left.isInt() && right.isFloat()) {
            setLeftOperand(new ConvFloat(getLeftOperand()));
            setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }
        if (left.isFloat() && right.isInt()) {
            setRightOperand(new ConvFloat(getRightOperand()));
            setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }
        if (left.isBoolean() && right.isBoolean()) {
            setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }
        throw new ContextualError("Operations non compatible pour la comparison", getLocation());
    }

    protected abstract void branchingInstructions(DecacCompiler compiler, fr.ensimag.ima.pseudocode.Label labelTrue,
         fr.ensimag.ima.pseudocode.Label labelFalse);
    
    protected void codeGenBool(DecacCompiler compiler, fr.ensimag.ima.pseudocode.GPRegister r,
        fr.ensimag.ima.pseudocode.Label labelTrue, fr.ensimag.ima.pseudocode.Label labelFalse
    ) {
        getLeftOperand().codeGenExpr(compiler, r);
        fr.ensimag.ima.pseudocode.GPRegister temp = compiler.getRegisterManager().allocateRegister();
        if (temp != null) {
            getRightOperand().codeGenExpr(compiler, temp);
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.CMP(temp, r));
            compiler.getRegisterManager().freeRegister(temp);
        } else {
            // Empiler le registre r
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.PUSH(r));
            getRightOperand().codeGenExpr(compiler, r);
            // Dépiler dans un registre temporaire R0
            fr.ensimag.ima.pseudocode.GPRegister r0 = fr.ensimag.ima.pseudocode.GPRegister.R0;
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(r, r0));
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.POP(r));
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.CMP(r0, r));
            
        }
        this.branchingInstructions(compiler, labelTrue, labelFalse);

        
        return;
    }
}


