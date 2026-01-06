package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;

/**
 *
 * @author gl10
 * @date 01/01/2026
 */
public class Greater extends AbstractOpIneq {

    public Greater(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return ">";
    }

    @Override
    protected void branchingInstructions(DecacCompiler compiler, fr.ensimag.ima.pseudocode.Label labelTrue,
         fr.ensimag.ima.pseudocode.Label labelFalse) {
        if(labelTrue != null) {
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BGT(labelTrue));
        }
        if (labelFalse != null) {
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BLE(labelFalse));
        }
    }

}
