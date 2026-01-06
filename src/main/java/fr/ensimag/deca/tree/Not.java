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
        Type type = this.getOperand().verifyExpr(compiler, localEnv, currentClass);
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

        fr.ensimag.ima.pseudocode.Label labelTrue = new fr.ensimag.ima.pseudocode.Label("E_True" + compiler.getLabelCounter());
        fr.ensimag.ima.pseudocode.Label labelEnd = new fr.ensimag.ima.pseudocode.Label("E_End" + compiler.getLabelCounter());
        // Générer le code pour l'opérande avec les labels inversés
        getOperand().codeGenBool(compiler, r, labelTrue, null);
        // Si l'opérande est faux, on met 1 dans r
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(1, r));
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BRA(labelEnd));
        // on met 0 dans r
        compiler.addLabel(labelTrue);
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(0, r));
        compiler.addLabel(labelEnd);
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler,
         fr.ensimag.ima.pseudocode.GPRegister r,
         fr.ensimag.ima.pseudocode.Label labelTrue, 
         fr.ensimag.ima.pseudocode.Label labelFalse
    ) {
        // Inverser les labels pour le not
        getOperand().codeGenBool(compiler, r, labelFalse, labelTrue);
    }
}
