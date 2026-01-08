package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 * 
 * @author gl10
 * @date 01/01/2026
 */
public class DeclClass extends AbstractDeclClass {
    private AbstractIdentifier className;
    private AbstractIdentifier superClass;
    private ListDeclField fields;
    private ListDeclMethod methods;

    public DeclClass(AbstractIdentifier className,
            AbstractIdentifier superClass,
            ListDeclField fields,
            ListDeclMethod methods) {
        this.className = className;
        this.superClass = superClass;
        this.fields = fields;
        this.methods = methods;
    }

    public AbstractIdentifier getClassName() {
        return className;
    }
    public AbstractIdentifier getSuperClass() {
        return superClass;
    }
    public ListDeclField getFields() {
        return fields;
    }
    public ListDeclMethod getMethods() {
        return methods;
    }
    

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class { ... A FAIRE ... }");
    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }
    
    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        throw new UnsupportedOperationException("Not yet supported");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        throw new UnsupportedOperationException("Not yet supported");
    }

    @Override
    public void codeGenClass(DecacCompiler compiler) {
        // A FAIRE: compléter ce squelette très rudimentaire de code
        // code pour les champs et autres initialisations
        // code pour les méthodes de chaque classe
        
        
    }

    @Override
    public void codeGenMethTable(DecacCompiler compiler) {
        // A FAIRE: compléter ce squelette très rudimentaire de code

        this.className.getName();
    }

}
