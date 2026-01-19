package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.WFLOAT;
import fr.ensimag.ima.pseudocode.instructions.WFLOATX;
import fr.ensimag.ima.pseudocode.instructions.WINT;


/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl10
 * @date 01/01/2026
 */
public abstract class AbstractExpr extends AbstractInst {
    /**
     * @return true if the expression does not correspond to any concrete token
     * in the source code (and should be decompiled to the empty string).
     */
    boolean isImplicit() {
        return false;
    }

    /**
     * Get the type decoration associated to this expression (i.e. the type computed by contextual verification).
     */
    public Type getType() {
        return type;
    }

    protected void setType(Type type) {
        Validate.notNull(type);
        this.type = type;
    }
    private Type type;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration");
        }
    }

    /**
     * Verify the expression for contextual error.
     * 
     * implements non-terminals "expr" and "lvalue" 
     *    of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return the Type of the expression
     *            (corresponds to the "type" attribute)
     */
    public abstract Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    /**
     * Verify the expression in right hand-side of (implicit) assignments 
     * 
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute            
     * @return this with an additional ConvFloat if needed...
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, 
            Type expectedType)
            throws ContextualError {
        Type actualType = verifyExpr(compiler, localEnv, currentClass);
        if (expectedType.isFloat() && actualType.isInt()) {
            ConvFloat conv = new ConvFloat(this);
            conv.setLocation(getLocation());
            conv.setLocation(getLocation());
            conv.verifyExpr(compiler, localEnv, currentClass);
            return conv;
        }
        if (!actualType.isAssignableTo(expectedType)) {
            throw new ContextualError("Incompatible assignment: expected " + expectedType + " but got " + actualType, getLocation());
        }
        return this;
    }
    
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        verifyExpr(compiler, localEnv, currentClass);
    }

    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv
     *            Environment in which the condition should be checked.
     * @param currentClass
     *            Definition of the class containing the expression, or null in
     *            the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type t = verifyExpr(compiler, localEnv, currentClass);
        if (!t.isBoolean()) {
            throw new ContextualError("Condition must be boolean", getLocation());
        }
    }

    /**
     * Generate code to print the expression
     *
     * @param compiler
     * @param printHex if true, floats should be displayed in hexadecimal format
     */
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) {
        // Allouer un registre dynamiquement au lieu d'utiliser R1 en dur
        GPRegister r = compiler.getRegisterManager().allocateRegister();
        if (r == null) {
            r = Register.R1; // Fallback sur R1 si aucun registre disponible
        }
        this.codeGenExpr(compiler, r);
        compiler.addInstruction(new LOAD(r, Register.R1));
        if (this.getType().isInt()) {
            compiler.addInstruction(new WINT());
        } else {
            if (printHex) {
                compiler.addInstruction(new WFLOATX());
            } else {
                compiler.addInstruction(new WFLOAT());
            }
        }
        // Libérer le registre s'il a été alloué
        if (r != Register.R1) {
            compiler.getRegisterManager().freeRegister(r);
        }
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        // Allouer un registre dynamiquement au lieu d'utiliser R0 en dur
        GPRegister r = compiler.getRegisterManager().allocateRegister();
        if (r == null) {
            r = Register.R0; // Fallback sur R0 si aucun registre disponible
        }
        codeGenExpr(compiler, r);
        // Libérer le registre s'il a été alloué
        if (r != Register.R0) {
            compiler.getRegisterManager().freeRegister(r);
        }
    }
    

    @Override
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }

    protected void codeGenExpr(DecacCompiler compiler, fr.ensimag.ima.pseudocode.GPRegister r){
        // laisser vide, sera redéfini dans les classes filles qui en ont besoin
    }

    @Override
    public int maxTemp() {
        // Par défaut, une expression n'a pas besoin de temporaire
        // Les sous-classes qui en ont besoin (comme MethodCall, BinaryExpr) surchargeront cette méthode
        return 0;
    }

    protected void codeGenBool(DecacCompiler compiler,
         fr.ensimag.ima.pseudocode.GPRegister r,
         fr.ensimag.ima.pseudocode.Label labelTrue, 
         fr.ensimag.ima.pseudocode.Label labelFalse
    ) {
        // laisser vide, sera redéfini dans les classes filles qui en ont besoin
    }

}




