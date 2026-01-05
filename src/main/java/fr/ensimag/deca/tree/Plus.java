package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

/**
 * @author gl10
 * @date 01/01/2026
 */
public class Plus extends AbstractOpArith {
    public Plus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }
 

    @Override
    protected String getOperatorName() {
        return "+";
    }
    
    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        // 1. Charger x dans le registre cible r (ex: R2)
        getLeftOperand().codeGenExpr(compiler, r); 

        // 2. Utiliser un registre si disponible, sinon empiler
        GPRegister r2=compiler.getRegisterManager().allocateRegister();
        if(r2!=null) {
            getRightOperand().codeGenExpr(compiler, r2);
            compiler.addInstruction(new ADD(r2, r));
            compiler.getRegisterManager().freeRegister(r2);
        } else {
            // Empiler le registre r
            compiler.addInstruction(new PUSH(r));
            getRightOperand().codeGenExpr(compiler, r); 
            // Dépiler dans un registre temporaire R0
            GPRegister r0 = GPRegister.R0;
            compiler.addInstruction(new LOAD(r,r0));
            compiler.addInstruction(new POP(r));
            compiler.addInstruction(new ADD(r0, r));
            return;
            
        }
    }

}


