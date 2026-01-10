package fr.ensimag.deca.tree;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import java.util.List;


public class ListDeclMethod extends TreeList<AbstractDeclMethod> {
    
    public void decompile(fr.ensimag.deca.tools.IndentPrintStream s) {
        for (AbstractDeclMethod m : getList()) {
            m.decompile(s);
            s.println();
        }
    }

}
