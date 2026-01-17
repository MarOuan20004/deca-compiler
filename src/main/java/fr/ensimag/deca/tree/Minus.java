package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateFloat;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.ADD;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.MUL;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.SUB;

/**
 * @author gl10
 * @date 01/01/2026
 */
public class Minus extends AbstractOpArith {
    public Minus(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "-";
    }
@Override
protected void codeGenExpr(DecacCompiler compiler, GPRegister r) {
    // 1. Calcul de l'opérande gauche dans r
    getLeftOperand().codeGenExpr(compiler, r);

    // 2. Tentative d'allocation d'un registre temporaire
    GPRegister r2 = compiler.getRegisterManager().allocateRegister();

    if (r2 != null) {
        // === CAS 1 : Il reste des registres disponibles ===
        
        // Calcul de droite dans le nouveau registre r2
        getRightOperand().codeGenExpr(compiler, r2);
        
        // Opération : r = r - r2 (Gauche - Droite)
        compiler.addInstruction(new SUB(r2, r));
        compiler.addInstruction(new BOV(new Label("arithmetic_overflow")));
        
        // Libération du registre temporaire
        compiler.getRegisterManager().freeRegister(r2);
        
    } else {
        // === CAS 2 : Plus de registres (Utilisation de la Pile et R0) ===
        
        // On sauvegarde l'opérande gauche (qui est dans r) sur la pile
        compiler.addInstruction(new PUSH(r));
        
        // On calcule l'opérande droite DANS r (on réutilise le registre)
        getRightOperand().codeGenExpr(compiler, r);
        
        // On récupère l'opérande gauche dans R0 (registre de travail)
        compiler.addInstruction(new LOAD(r, GPRegister.R0)); // Sauvegarde temporaire de Droite dans R0 ?? NON
        // ATTENTION : La logique standard est celle-ci :
        
        compiler.addInstruction(new POP(GPRegister.R0)); // R0 = Gauche (depuis la pile)
        
        // Opération : R0 = R0 - r (Gauche - Droite)
        compiler.addInstruction(new SUB(r, GPRegister.R0));
        compiler.addInstruction(new BOV(new Label("arithmetic_overflow")));
        
        // On remet le résultat (qui est dans R0) dans le registre cible r
        compiler.addInstruction(new LOAD(GPRegister.R0, r));
    }
}   
}
