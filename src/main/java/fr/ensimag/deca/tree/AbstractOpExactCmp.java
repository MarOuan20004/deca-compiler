package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;

/**
 *
 * @author gl10
 * @date 01/01/2026
 */
public abstract class AbstractOpExactCmp extends AbstractOpCmp {

    public AbstractOpExactCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    
    protected void codeGenExpr(DecacCompiler compiler, fr.ensimag.ima.pseudocode.GPRegister r,
        fr.ensimag.ima.pseudocode.Label labelTrue, fr.ensimag.ima.pseudocode.Label labelFalse
    ) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
