package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Conversion of an int into a float. Used for implicit conversions.
 * 
 * @author gl10
 * @date 01/01/2026
 */
public class ConvFloat extends AbstractUnaryExpr {
    public ConvFloat(AbstractExpr operand) {
        super(operand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        ConvFloat conv = new ConvFloat(getOperand());
        conv.setType(compiler.environmentType.FLOAT);
        return conv.getType();
    }


    @Override
    protected String getOperatorName() {
        return "/* conv float */";
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, fr.ensimag.ima.pseudocode.GPRegister r) {
        // Générer le code de l'opérande dans un registre temporaire
        fr.ensimag.ima.pseudocode.GPRegister tempReg = compiler.getRegisterManager().allocateRegister();
        getOperand().codeGenExpr(compiler, tempReg);
        // Ajouter l'instruction de conversion
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.FLOAT(tempReg, r));
        // Libérer le registre temporaire
        compiler.getRegisterManager().freeRegister(tempReg);
    }

}
