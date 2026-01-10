package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * List of field declarations.
 *
 * @author gl10
 * @date 01/01/2026
 */

public class ListDeclField extends TreeList<AbstractDeclField> {
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField declField : getList()) {
            declField.decompile(s);
            s.println();
        }
    }

    /**
     * Implements non-terminal "list_decl_field" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains the "env_types" attribute
     * @param currentClass 
     *          corresponds to "class" attribute (null in the main bloc).
     */
    void verifyListDeclField(DecacCompiler compiler, ClassDefinition currentClass)
            throws ContextualError {
        for (AbstractDeclField declField : getList()) {
            declField.verifyDeclField(compiler, currentClass);
        }
}
}