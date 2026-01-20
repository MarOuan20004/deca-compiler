package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

public class ListDeclFunction extends TreeList<AbstractDeclFunction> {

    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclFunction f : getList()) {
            f.decompile(s);
            s.println();
        }
    }

    public void verifyListDeclFunction(DecacCompiler compiler) throws ContextualError {
        for (AbstractDeclFunction f : getList()) {
            f.verifyDeclFunction(compiler);
        }
    }

    public void verifyListDeclFunctionBody(DecacCompiler compiler) throws ContextualError {
        for (AbstractDeclFunction f : getList()) {
            f.verifyDeclFunctionBody(compiler);
        }
    }

    public void codeGenListDeclFunction(DecacCompiler compiler) {
        for (AbstractDeclFunction f : getList()) {
            f.codeGenDeclFunction(compiler);
        }
    }
}
