package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;

/**
 *
 * @author gl10
 * @date 01/01/2026
 */
public class NotEquals extends AbstractOpExactCmp {

    public NotEquals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "!=";
    }

    @Override
    protected void branchingInstructions(DecacCompiler compiler, fr.ensimag.ima.pseudocode.Label labelTrue,
         fr.ensimag.ima.pseudocode.Label labelFalse) {
        if(labelTrue != null) {
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BNE(labelTrue));
        }
        if (labelFalse != null) {
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BEQ(labelFalse));
        }
    }

}
