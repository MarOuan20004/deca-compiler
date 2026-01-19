package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Arithmetic binary operations (+, -, /, ...)
 * 
 * @author gl10
 * @date 01/01/2026
 */
public abstract class AbstractOpArith extends AbstractBinaryExpr {

    public AbstractOpArith(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        Type left = this.getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type right = this.getRightOperand().verifyExpr(compiler, localEnv, currentClass);

        if ((left.isFloat() && right.isFloat()) || (left.isInt() && right.isInt())) {
            this.setType(left);
            return this.getType();
        } 
        else if (left.isFloat() && right.isInt()) {
            // convert right operand to float
            ConvFloat conv = new ConvFloat(this.getRightOperand());
            conv.setLocation(this.getRightOperand().getLocation());
            this.setRightOperand(conv);
            conv.verifyExpr(compiler, localEnv, currentClass);
            this.setType(left);
            return this.getType();
        } 
        else if (left.isInt() && right.isFloat()) {
            // convert left operand to float
            ConvFloat conv = new ConvFloat(this.getLeftOperand());
            conv.setLocation(this.getLeftOperand().getLocation());
            conv.setLocation(this.getLeftOperand().getLocation());
            this.setLeftOperand(conv);
            conv.verifyExpr(compiler, localEnv, currentClass);
            this.setType(right);
            return this.getType();
        } 
        else {
            throw new ContextualError("Incompatible types for arithmetic operation: "
                    + left + " and " + right, this.getLocation());
        }
    }
}
