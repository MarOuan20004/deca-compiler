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


}
