package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.MUL;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

/**
 * @author gl10
 * @date 01/01/2026
 */
public class Multiply extends AbstractOpArith {
    public Multiply(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "*";
    }

     @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister target) {
        // évaluer l'opérande gauche
        getLeftOperand().codeGenExpr(compiler, GPRegister.R0);
        compiler.addInstruction(new PUSH(GPRegister.R0)); // sauvegarder temporaire

        // évaluer l'opérande droit
        getRightOperand().codeGenExpr(compiler, GPRegister.R0);

        // récupérer opérande gauche
        compiler.addInstruction(new POP(GPRegister.R1));

        // appliquer l'opération
        compiler.addInstruction(new MUL(GPRegister.R1, GPRegister.R0)); 
        // Copier le résultat dans target si besoin
        if (target != GPRegister.R0) {
            compiler.addInstruction(new LOAD(GPRegister.R0, target));
        }

    }

}
