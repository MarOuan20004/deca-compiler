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
public class Modulo extends AbstractOpArith {

    public Modulo(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        // applicable only to int types
        Type leftType = getLeftOperand().getType();
        Type rightType = getRightOperand().getType();
        if (leftType != compiler.environmentType.INT || rightType != compiler.environmentType.INT) {
            throw new ContextualError("Modulo operator can only be applied to int types", this.getLocation());
        }
        this.setType(compiler.environmentType.INT);
        return compiler.environmentType.INT;
    }


    @Override
    protected String getOperatorName() {
        return "%";
    }

}
