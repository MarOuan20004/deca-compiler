package fr.ensimag.deca.tree;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.ima.pseudocode.Label;


public class ListDeclMethod extends TreeList<AbstractDeclMethod> {
    
    public void decompile(fr.ensimag.deca.tools.IndentPrintStream s) {
        for (AbstractDeclMethod m : getList()) {
            m.decompile(s);
            s.println();
        }
    }

    /**
     * Implements non-terminal "list_decl_method" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param currentClass 
     *          corresponds to "class" attribute (null in the main bloc).
     */
    void verifyListDeclMethod(DecacCompiler compiler, fr.ensimag.deca.context.ClassDefinition currentClass)
            throws ContextualError {
        for (AbstractDeclMethod m : getList()) {
            m .verifyDeclMethod(compiler, currentClass);
        }
    
    }
    void verifyListDeclMethodBody(DecacCompiler compiler, fr.ensimag.deca.context.ClassDefinition currentClass)
        throws ContextualError {
        for (AbstractDeclMethod method : getList()) {
            ((DeclMethod) method).verifyDeclMethodBody(compiler, currentClass);
        }
    }

    void codeGenListDeclMethod(DecacCompiler compiler, ClassDefinition currentClass) {
        for (AbstractDeclMethod m : getList()){
            DeclMethod method = (DeclMethod) m;
            // code de l'étiquette de la méthode
            String methodName = method.getMethodName().getName().getName();
            String className = currentClass.getType().getName().getName();
            Label methodLabel = new Label("Code." + className + "." + methodName);
            compiler.addLabel(methodLabel);

            method.codeGenDeclMethod(compiler);
            // ajouter l'instruction de fin de méthode
            String endString = "fin." + className + "." + methodName;
            compiler.addLabel(new Label(endString));

        }
    }


}
