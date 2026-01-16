package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.ParamDefinition;

/**
 * Assignment, i.e. lvalue = expr.
 *
 * @author gl10
 * @date 01/01/2026
 */
public class Assign extends AbstractBinaryExpr {

    @Override
    public AbstractLValue getLeftOperand() {
        // The cast succeeds by construction, as the leftOperand has been set
        // as an AbstractLValue by the constructor.
        return (AbstractLValue)super.getLeftOperand();
    }

    public Assign(AbstractLValue leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type leftType = getLeftOperand().verifyExpr(compiler, localEnv, currentClass);
        AbstractExpr right = getRightOperand().verifyRValue(compiler, localEnv, currentClass, leftType);
        setRightOperand(right);
        setType(leftType);
        return leftType;
    }


    @Override
    protected String getOperatorName() {
        return "=";
    }

    @Override
public void codeGenInst(DecacCompiler compiler) {
    // 1. Générer le code de l'expression de droite (RHS)
    GPRegister rightReg = compiler.getRegisterManager().allocateRegister();
    getRightOperand().codeGenExpr(compiler, rightReg);

    // 2. Identifier le type de l'opérande de gauche (LHS)
    if (getLeftOperand() instanceof AbstractIdentifier) {
        AbstractIdentifier ident = (AbstractIdentifier) getLeftOperand();
        Definition def = ident.getDefinition();

        if (def.isField()) {
            // Cas : x = ... (implicitement this.x = ...) dans une méthode
            FieldDefinition fieldDef = (FieldDefinition) def;
            GPRegister objectReg = compiler.getRegisterManager().allocateRegister();

            // On charge 'this' (toujours en -2(LB))
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), objectReg));

            // On stocke la valeur dans le champ : Mem[this + index] = val
            compiler.addInstruction(new STORE(rightReg, new RegisterOffset(fieldDef.getIndex(), objectReg)));

            compiler.getRegisterManager().freeRegister(objectReg);
        } else {
            // Cas : Variable locale ou Paramètre ou Variable Globale
            // getOperand() renvoie l'adresse (ex: -3(LB), 1(GB)...)
            // Note: definition doit être VariableDefinition ou ParamDefinition
            if (def instanceof ParamDefinition){
                ParamDefinition paramDef = (ParamDefinition) def;
                DAddr addr = paramDef.getOperand(); 
                compiler.addInstruction(new STORE(rightReg, addr));
                }
            else if (def instanceof fr.ensimag.deca.context.VariableDefinition){
                fr.ensimag.deca.context.VariableDefinition varDef = (fr.ensimag.deca.context.VariableDefinition) def;
                DAddr addr = varDef.getOperand(); 
                compiler.addInstruction(new STORE(rightReg, addr));
            }
            
        }

    } else if (getLeftOperand() instanceof Selection) {
        // Cas : obj.x = ...
        // Tu dois appeler une méthode spécifique dans Selection pour gérer l'assignation
        // car il faut évaluer 'obj', vérifier qu'il n'est pas null, puis faire le STORE.
        ((Selection) getLeftOperand()).codeGenAssign(compiler, rightReg);
    }

    // 3. Libération
    compiler.getRegisterManager().freeRegister(rightReg);
}

}
