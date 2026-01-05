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
public class Not extends AbstractUnaryExpr {

    public Not(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type type = this.getOperand().getType();
        if (type != compiler.environmentType.BOOLEAN) {
            throw new ContextualError("Logical not can only be applied to boolean", this.getLocation());
        }
        this.setType(type);
        return type;
    }


    @Override
    protected String getOperatorName() {
        return "!";
    }
    @Override
    protected void codeGenExpr(DecacCompiler compiler, fr.ensimag.ima.pseudocode.GPRegister r) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
