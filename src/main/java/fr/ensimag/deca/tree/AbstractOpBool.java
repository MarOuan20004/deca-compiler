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
public abstract class AbstractOpBool extends AbstractBinaryExpr {

    public AbstractOpBool(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Type left=getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type right=getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if (left.isBoolean() && right.isBoolean()) {
            setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }
        throw new ContextualError("Operations non compatibles pour les booléens", getLocation());
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, fr.ensimag.ima.pseudocode.GPRegister r) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
