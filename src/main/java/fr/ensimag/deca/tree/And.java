package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;

/**
 *
 * @author gl10
 * @date 01/01/2026
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, GPRegister r,
            fr.ensimag.ima.pseudocode.Label labelTrue,
            fr.ensimag.ima.pseudocode.Label labelFalse) {
        
        fr.ensimag.ima.pseudocode.Label labelMid = new fr.ensimag.ima.pseudocode.Label("E_Fin" + compiler.getLabelCounter());
        // Générer le code pour le premier opérande
        getLeftOperand().codeGenBool(compiler, r, labelFalse, labelMid);
        // Générer le code pour le deuxième opérande
        getRightOperand().codeGenBool(compiler, r, labelTrue, labelFalse);
        // Ajouter le label de fin
        compiler.addLabel(labelMid);

    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        getLeftOperand().codeGenExpr(compiler, r);
        fr.ensimag.ima.pseudocode.Label labelFalse = new fr.ensimag.ima.pseudocode.Label("E_False" + compiler.getLabelCounter());
        fr.ensimag.ima.pseudocode.Label labelEnd = new fr.ensimag.ima.pseudocode.Label("E_End" + compiler.getLabelCounter());
        // Si le premier opérande est faux, on saute à labelFalse
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BEQ(labelFalse));
        // Sinon, on évalue le deuxième opérande
        getRightOperand().codeGenExpr(compiler, r);
        // Si le deuxième opérande est faux, on saute à labelFalse
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BEQ(labelFalse));
        // Si on arrive ici, les deux opérandes sont vrais, on met 1 dans r
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(1, r));
        // On saute à la fin
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BRA(labelEnd));
        // labelFalse : on met 0 dans r
        compiler.addLabel(labelFalse);
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(0, r));
        // labelEnd : fin de l'opération
        compiler.addLabel(labelEnd);
    }
}