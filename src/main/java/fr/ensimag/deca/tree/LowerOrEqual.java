package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;

/**
 *
 * @author gl10
 * @date 01/01/2026
 */
public class LowerOrEqual extends AbstractOpIneq {
    public LowerOrEqual(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "<=";
    }

    @Override
    protected void branchingInstructions(DecacCompiler compiler, fr.ensimag.ima.pseudocode.Label labelTrue,
         fr.ensimag.ima.pseudocode.Label labelFalse) {
        
        if(labelTrue != null) {
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BLE(labelTrue));
        }
        if (labelFalse != null) {
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BGT(labelFalse));
        }
    }
    
}