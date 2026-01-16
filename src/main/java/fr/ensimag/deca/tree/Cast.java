package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.deca.context.ClassType ;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.FLOAT;
import fr.ensimag.ima.pseudocode.instructions.WSTR;
import fr.ensimag.ima.pseudocode.instructions.ERROR;

/**
 * Cast expression (e.g. (int) x).
 *
 * @author gl10
 * @date 13/01/2026
 */

public class Cast extends AbstractExpr {

    private final AbstractIdentifier type;
    private final AbstractExpr expr;

    public Cast(AbstractIdentifier type, AbstractExpr expr) {
        Validate.notNull(type);
        Validate.notNull(expr);
        this.type = type;
        this.expr = expr;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type targetType = type.verifyType(compiler);
        Type exprType = expr.verifyExpr(compiler, localEnv, currentClass);

        if (targetType.isVoid()) {
            throw new ContextualError("Cannot cast to void", getLocation());
        }

        if (exprType.sameType(targetType)) {
            // Casting to the same type is always allowed
            this.setType(targetType);
            return targetType;
        }

        if (exprType.isInt() && targetType.isFloat()) {
            this.setType(targetType);
            return targetType;
        }
        

        if (exprType.isClass() && targetType.isClass()) {
            ClassType exprClassType = ((ClassType)exprType).asClassType("", getLocation());
            ClassType targetClassType = ((ClassType)targetType).asClassType("", getLocation());
            // check subclass
            if (exprClassType.isSubClassOf(targetClassType)) {
                this.setType(targetType);
                return targetType;
            }
            // check superclass
            if (targetClassType.isSubClassOf(exprClassType)) {
                this.setType(targetType);
                return targetType;
            }
        }
        
        if (exprType.isNull() && targetType.isClass()) {
            this.setType(targetType);
            return targetType;
        }

        throw new ContextualError("Invalid cast from " + exprType.getName() + " to " + targetType.getName(), getLocation());
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        Type targetType = type.getType();
        Type exprType = expr.getType();

        if (exprType.isInt() && targetType.isFloat()) {
            expr.codeGenExpr(compiler, r);
            compiler.addInstruction(new FLOAT(r, r));
        } else if (exprType.isClassOrNull() && targetType.isClass()) {
            expr.codeGenExpr(compiler, r);
            
            // Check if upcast
            if (exprType.isNull() || ((ClassType)exprType).isSubClassOf((ClassType)targetType)) {
                // Nothing to do
                return;
            }
            
            // Downcast: need runtime check
            int id = compiler.getLabelCounter();
            Label loopLabel = new Label("cast_loop_" + id);
            Label trueLabel = new Label("cast_true_" + id);
            Label endLabel = new Label("cast_end_" + id);

            // Check if null
            compiler.addInstruction(new CMP(new NullOperand(), r));
            compiler.addInstruction(new BEQ(endLabel));

            GPRegister r1 = compiler.getRegisterManager().allocateRegister();
            // Get method table address of the object
            compiler.addInstruction(new LOAD(new RegisterOffset(0, r), r1));

            compiler.addLabel(loopLabel);
            // Get method table address of the target class
            compiler.addInstruction(new LEA(type.getClassDefinition().getAdrTable(), Register.R0));
            compiler.addInstruction(new CMP(Register.R0, r1));
            compiler.addInstruction(new BEQ(trueLabel));

            // Get parent method table
            compiler.addInstruction(new LOAD(new RegisterOffset(0, r1), r1));
            compiler.addInstruction(new CMP(new NullOperand(), r1));
            compiler.addInstruction(new BNE(loopLabel));

            // Error
            compiler.addInstruction(new WSTR("Error: Invalid cast"));
            compiler.addInstruction(new ERROR());

            compiler.addLabel(trueLabel);
            compiler.getRegisterManager().freeRegister(r1);
            compiler.addLabel(endLabel);
        } else {
            // Same type or other handled by verifyExpr
            expr.codeGenExpr(compiler, r);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        type.decompile(s);
        s.print(")(");
        expr.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        expr.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        expr.iter(f);
    }

}
