package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.DIV;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.MUL;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.QUO;
import fr.ensimag.ima.pseudocode.instructions.SUB;

/**
 *
 * @author gl10
 * @date 01/01/2026
 */
public class Divide extends AbstractOpArith {
    public Divide(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "/";
    }


    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        // Calcul de l’opérande gauche dans r
        getLeftOperand().codeGenExpr(compiler, r);

        GPRegister r2 = compiler.getRegisterManager().allocateRegister();

        if (r2 != null) {
            // Cas registre
            getRightOperand().codeGenExpr(compiler, r2);
            compiler.addInstruction(new DIV(r2, r)); // r = gauche / droite
            compiler.getRegisterManager().freeRegister(r2);
        } else {
            // Cas pile
            compiler.addInstruction(new PUSH(r));          // sauver gauche
            getRightOperand().codeGenExpr(compiler, r);    // r = droite
            compiler.addInstruction(new POP(GPRegister.R0)); // R0 = gauche
            if(getType().isInt()) {
                compiler.addInstruction(new QUO(GPRegister.R0, r));
            }
            else {
                compiler.addInstruction(new DIV(r, GPRegister.R0)); // R0 = gauche / droite
            }
                compiler.addInstruction(new LOAD(GPRegister.R0, r)); // r = résultat
        }
    }
}
