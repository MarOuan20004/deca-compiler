package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;

/**
 *
 * @author gl10
 * @date 01/01/2026
 */
public class Lower extends AbstractOpIneq {

    public Lower(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "<";
    }

    @Override
    protected void branchingInstructions(DecacCompiler compiler, fr.ensimag.ima.pseudocode.Label labelTrue,
         fr.ensimag.ima.pseudocode.Label labelFalse) {
        // Si leftOperand < rightOperand, on saute à labelTrue
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BLT(labelTrue));
        // Sinon, on saute à labelFalse
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BGE(labelFalse));
    }

}
