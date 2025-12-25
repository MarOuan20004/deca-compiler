package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
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


}
