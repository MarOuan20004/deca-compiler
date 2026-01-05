package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.instructions.BNE;

/**
 *
 * @author gl10
 * @date 01/01/2026
 */
public class Equals extends AbstractOpExactCmp {

    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "==";
    }

    @Override
    protected void branchingInstructions(DecacCompiler compiler, fr.ensimag.ima.pseudocode.Label labelTrue,
         fr.ensimag.ima.pseudocode.Label labelFalse) {
        // Si les valeurs sont égales, on saute à labelTrue
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BEQ(labelTrue));
        // Sinon, on saute à labelFalse
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BNE(labelFalse));
    }
    
}
