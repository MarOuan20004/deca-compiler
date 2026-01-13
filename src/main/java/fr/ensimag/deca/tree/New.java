package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;


/**
 *
 * @author gl10
 * @date 13/01/2026
 */

public class New extends AbstractExpr{
    private AbstractIdentifier classNom;

    public New(AbstractIdentifier classNom) {
        this.classNom = classNom;
    }
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
                Type typeClass = classNom.verifyType(compiler) ;
        if(!typeClass.isClass()){
            throw new ContextualError("Only a class can use new ", classNom.getLocation());
        }
        this.setType(typeClass);
        return typeClass;
        }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        classNom.decompile(s);
        s.print("()");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    public void codeGenExpr(DecacCompiler compiler, fr.ensimag.ima.pseudocode.GPRegister r) {
        //TODO
    }
}
    

