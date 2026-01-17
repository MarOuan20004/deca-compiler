package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.SUB;

/**
 * @author gl10
 * @date 01/01/2026
 */
public class UnaryMinus extends AbstractUnaryExpr {

    public UnaryMinus(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type = this.getOperand().verifyExpr(compiler, localEnv, currentClass);
        if (type != compiler.environmentType.INT && type != compiler.environmentType.FLOAT) {
            throw new ContextualError("Unary minus can only be applied to int or float", this.getLocation());
        }
        this.setType(type);
        return type;
    }


    @Override
    protected String getOperatorName() {
        return "-";
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        // Compute the operand in r
        getOperand().codeGenExpr(compiler, r);
        
        // To compute -(value), we need: 0 - value
        // In IMA: SUB(op1, op2) computes op2 = op2 - op1
        // So we need to load 0 into another register first
        
        GPRegister temp = compiler.getRegisterManager().allocateRegister();
        
        if (temp != null) {
            // We have a temporary register available
            if (getType().isFloat()) {
                compiler.addInstruction(new LOAD(new ImmediateFloat(0.0f), temp));
            } else {
                compiler.addInstruction(new LOAD(new ImmediateInteger(0), temp));
            }
            // Now SUB(r, temp) means temp = temp - r = 0 - r = -r
            compiler.addInstruction(new SUB(r, temp));
            // Move result back to r
            compiler.addInstruction(new LOAD(temp, r));
            compiler.getRegisterManager().freeRegister(temp);
        } else {
            // No temp register available - this shouldn't happen for unary operations
            // but we can use R0 as a temporary
            temp = GPRegister.R0;
            if (getType().isFloat()) {
                compiler.addInstruction(new LOAD(new ImmediateFloat(0.0f), temp));
            } else {
                compiler.addInstruction(new LOAD(new ImmediateInteger(0), temp));
            }
            compiler.addInstruction(new SUB(r, temp));
            compiler.addInstruction(new LOAD(temp, r));
        }
    }

}
