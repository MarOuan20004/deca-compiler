package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.RTS;

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
//Méthode pour pass 3
    void verifyListDeclFieldBody(DecacCompiler compiler, ClassDefinition currentClass)
            throws ContextualError {
        for (AbstractDeclField field : getList()) {
            ((DeclField) field).verifyDeclFieldBody(compiler, currentClass);
        }
    }

    void codeGenListDeclFieldInit(DecacCompiler compiler) {


        // champs propre à la classe
        for (AbstractDeclField absField : getList()) {
            DeclField field = (DeclField) absField;
            field.codeGenInitField(compiler, field.getFieldName().getFieldDefinition().getIndex());
        }
    }

    void codeGenListDeclField(DecacCompiler compiler) {

        for (AbstractDeclField absField : getList()) {
            DeclField field = (DeclField) absField;
            if (field.getInitialization() != null) {
                field.codeGenDeclField(compiler, field.getFieldName().getFieldDefinition().getIndex());
            }
        }
        compiler.addInstruction(new RTS());
    }

}