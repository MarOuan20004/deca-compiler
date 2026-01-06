package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;

/**
 *
 * @author gl10
 * @date 01/01/2026
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }

    @Override
    protected void codeGenBool(DecacCompiler compiler, fr.ensimag.ima.pseudocode.GPRegister r,
            fr.ensimag.ima.pseudocode.Label labelTrue,
            fr.ensimag.ima.pseudocode.Label labelFalse) {
        
        getLeftOperand().codeGenBool(compiler, r, labelTrue, null); 
        getRightOperand().codeGenBool(compiler, r, labelTrue, labelFalse);

    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, fr.ensimag.ima.pseudocode.GPRegister r) {
        
        fr.ensimag.ima.pseudocode.Label labelTrue = new fr.ensimag.ima.pseudocode.Label("E_True" + compiler.getLabelCounter());
        fr.ensimag.ima.pseudocode.Label labelEnd = new fr.ensimag.ima.pseudocode.Label("E_End" + compiler.getLabelCounter());
        fr.ensimag.ima.pseudocode.Label labelFalse = new fr.ensimag.ima.pseudocode.Label("E_False" + compiler.getLabelCounter());
        getLeftOperand().codeGenBool(compiler, r, labelTrue, null);
        // premier est faux, on évalue le deuxième
        getRightOperand().codeGenBool(compiler, r, labelTrue, labelFalse);
        // si le deuxième est vrai, on met 1 dans r ou le premier est vrai, on met 1 dans r
        compiler.addLabel(labelTrue);
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(1, r));
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BRA(labelEnd));
        // on met 0 dans r
        compiler.addLabel(labelFalse);
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(0, r));
        // fin de l'opération
        compiler.addLabel(labelEnd);
    }

}