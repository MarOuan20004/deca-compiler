package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.REM;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
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
        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type rightType = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
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

   @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        // Calcul de l’opérande gauche dans r
        getLeftOperand().codeGenExpr(compiler, r);

        GPRegister r2 = compiler.getRegisterManager().allocateRegister();

        if (r2 != null) {
            // Cas registre
            getRightOperand().codeGenExpr(compiler, r2);
            compiler.addInstruction(new REM(r2, r)); // r = gauche % droite
            compiler.addInstruction(new BOV(new Label("division_by_zero")));
            compiler.getRegisterManager().freeRegister(r2);
        } else {
            // Cas pile
            compiler.addInstruction(new PUSH(r));          // sauver gauche
            getRightOperand().codeGenExpr(compiler, r);    // r = droite
            compiler.addInstruction(new POP(GPRegister.R0)); // R0 = gauche
            compiler.addInstruction(new REM(r, GPRegister.R0)); // R0 = gauche % droite
            compiler.addInstruction(new BOV(new Label("division_by_zero")));
            compiler.addInstruction(new LOAD(GPRegister.R0, r)); // r = résultat
        }
    }

}
