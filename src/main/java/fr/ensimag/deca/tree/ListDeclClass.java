package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.log4j.Logger;

/**
 *
 * @author gl10
 * @date 01/01/2026
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass: start");
        for (AbstractDeclClass c : getList()) {
            c.verifyClass(compiler);//on parcourt la liste des classes puis on vérifie la passe 1
            
        }
        LOG.debug("verify listClass: end");
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass: start");
        for (AbstractDeclClass c : getList()) {
            c.verifyClassMembers(compiler);//on parcourt la liste des classes puis on vérifie la passe 1
            
        }
        LOG.debug("verify listClass: end");
    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass: start");
        for (AbstractDeclClass c : getList()) {
            c.verifyClassBody(compiler);//on parcourt la liste des classes puis on vérifie la passe 1
            
        }
        LOG.debug("verify listClass: end");
    }

    public void codeGenListClass(DecacCompiler compiler) {
        for (AbstractDeclClass c : getList()) {
            ((DeclClass) c).codeGenClass(compiler);
        }
    }

    public void codeGenMethTable(DecacCompiler compiler) {
        int offset = 1;
        for (AbstractDeclClass c : getList()) {
            ((DeclClass) c).codeGenMethTable(compiler, offset);
            offset += ((DeclClass) c).getMethods().getList().size();
        }
    }


}
