package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;

/**
 * Instruction "return expr;"
 * @author gl10
 * @date 13/01/2026
 */
public class Return extends AbstractInst {
    private AbstractExpr exp;

    public Return(AbstractExpr exp) {
        Validate.notNull(exp);
        this.exp = exp;
    }

    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType) throws ContextualError {
        
        // 1. Verifier que nous ne sommes pas dans le Main (le return est interdit dans le main)
        if (returnType.isVoid() && currentClass == null) {
             // Note: En Deca, le main est considere comme retournant void
             // Mais un 'return' explicite y est interdit par la grammaire ou le contexte.
             throw new ContextualError("Return cannot be used in main program.", getLocation());
        }

        // 2. Verifier que l'expression retournee est compatible avec le type de retour de la methode
        // On utilise verifyRValue pour verifier la compatibilité (assign-compatible)
        exp.verifyRValue(compiler, localEnv, currentClass, returnType);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("return ");
        exp.decompile(s);
        s.print(";");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        exp.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        exp.prettyPrint(s, prefix, true);
    }
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        GPRegister reg = compiler.getRegisterManager().allocateRegister();
        exp.codeGenExpr(compiler,reg);
        
    }
}