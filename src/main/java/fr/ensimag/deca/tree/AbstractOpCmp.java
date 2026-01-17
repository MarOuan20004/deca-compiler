package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 *
 * @author gl10
 * @date 01/01/2026
 */
public abstract class AbstractOpCmp extends AbstractBinaryExpr {

    public AbstractOpCmp(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type left = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        Type right = getRightOperand().verifyExpr(compiler, localEnv, currentClass);
        if (left.isInt() && right.isInt()) {
            setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }
        if (left.isFloat() && right.isFloat()) {
            setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }
        if (left.isInt() && right.isFloat()) {
            ConvFloat convFloat = new ConvFloat(getLeftOperand());
            convFloat.setLocation(getLeftOperand().getLocation());
            setLeftOperand(convFloat);
            convFloat.verifyExpr(compiler, localEnv, currentClass);
            
            setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }
        if (left.isFloat() && right.isInt()) {
            ConvFloat convFloat = new ConvFloat(getRightOperand());
            convFloat.setLocation(getRightOperand().getLocation());
            setRightOperand(convFloat);
            convFloat.verifyExpr(compiler, localEnv, currentClass);
            
            setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }
        if (left.isBoolean() && right.isBoolean()) {
            setType(compiler.environmentType.BOOLEAN);
            return compiler.environmentType.BOOLEAN;
        }
        if (left.isClassOrNull() && right.isClassOrNull()) {
            if (this instanceof AbstractOpExactCmp) {
                setType(compiler.environmentType.BOOLEAN);
                return compiler.environmentType.BOOLEAN;
            }
        }
        throw new ContextualError("Operations non compatible pour la comparison", getLocation());
    }

    protected abstract void branchingInstructions(DecacCompiler compiler, fr.ensimag.ima.pseudocode.Label labelTrue,
         fr.ensimag.ima.pseudocode.Label labelFalse);
    
    protected void codeGenBool(DecacCompiler compiler,
        fr.ensimag.ima.pseudocode.GPRegister r,
        fr.ensimag.ima.pseudocode.Label labelTrue,
        fr.ensimag.ima.pseudocode.Label labelFalse
    ) {
        getLeftOperand().codeGenExpr(compiler, r);
        fr.ensimag.ima.pseudocode.GPRegister temp = compiler.getRegisterManager().allocateRegister();
        if (temp != null) {
            getRightOperand().codeGenExpr(compiler, temp);
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.CMP(temp, r));
            compiler.getRegisterManager().freeRegister(temp);
        } else {
            // Empiler le registre r
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.PUSH(r));
            getRightOperand().codeGenExpr(compiler, r);
            // Dépiler dans un registre temporaire R0
            fr.ensimag.ima.pseudocode.GPRegister r0 = fr.ensimag.ima.pseudocode.GPRegister.R0;
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(r, r0));
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.POP(r));
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.CMP(r0, r));    
        }
        this.branchingInstructions(compiler, labelTrue, labelFalse);

        
        return;
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        // Création de labels uniques pour le branchement
        fr.ensimag.ima.pseudocode.Label labelTrue = new fr.ensimag.ima.pseudocode.Label("E_True" + compiler.getLabelCounter());
        fr.ensimag.ima.pseudocode.Label labelEnd = new fr.ensimag.ima.pseudocode.Label("E_End" + compiler.getLabelCounter());
        // si la comparaison est vraie, on saute à labelTrue
        this.codeGenBool(compiler, r, labelTrue, null);
        // la comparaison a échoué
        compiler.addInstruction(new LOAD(0, r));
        compiler.addInstruction(new BRA(labelEnd));
        // la comparaison a réussi
        compiler.addLabel(labelTrue);
        compiler.addInstruction(new LOAD(1, r));

        compiler.addLabel(labelEnd);
    }
}


