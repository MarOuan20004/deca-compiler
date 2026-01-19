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
    protected void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        getRightOperand().codeGenExpr(compiler, r);
        if (getLeftOperand() instanceof AbstractIdentifier) {
            AbstractIdentifier ident = (AbstractIdentifier) getLeftOperand();
            Definition def = ident.getDefinition();
            if (def.isField()) {
                FieldDefinition fieldDef = (FieldDefinition) def;
                GPRegister objectReg = compiler.getRegisterManager().allocateRegister();
                if (objectReg != null) {
                    // On charge this
                    compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), objectReg));
                    // On stocke la valeur dans le champ : Mem[this + index] = val
                    compiler.addInstruction(new STORE(r, new RegisterOffset(fieldDef.getIndex(), objectReg)));
                    compiler.getRegisterManager().freeRegister(objectReg);
                }
            } else {
                if (def instanceof ParamDefinition){
                    ParamDefinition paramDef = (ParamDefinition) def;
                    DAddr addr = paramDef.getOperand(); 
                    compiler.addInstruction(new STORE(r, addr));
                }
                else if (def instanceof fr.ensimag.deca.context.VariableDefinition){
                    fr.ensimag.deca.context.VariableDefinition varDef = (fr.ensimag.deca.context.VariableDefinition) def;
                    DAddr addr = varDef.getOperand(); 
                    compiler.addInstruction(new STORE(r, addr));
                }
            }

        } else if (getLeftOperand() instanceof Selection) {
            ((Selection) getLeftOperand()).codeGenAssign(compiler, r);
        }

    }

}
