package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Declaration of a class field.
 *
 * @author gl10
 * @date 01/01/2026
 */
public class DeclField extends AbstractDeclField {
    private Visibility visibility;
    private AbstractIdentifier type;
    private AbstractIdentifier fieldName;
    private AbstractInitialization initialization;

    public DeclField(Visibility visibility, AbstractIdentifier type,
            AbstractIdentifier fieldName, AbstractInitialization initialization) {
        Validate.notNull(visibility);
        Validate.notNull(type);
        Validate.notNull(fieldName);
        Validate.notNull(initialization);
        this.visibility = visibility;
        this.type = type;
        this.fieldName = fieldName;
        this.initialization = initialization;
    }

    // Getters
    public Visibility getVisibility() {
        return visibility;
    }

    public AbstractIdentifier getType() {
        return type;
    }

    public AbstractIdentifier getFieldName() {
        return fieldName;
    }

    public AbstractInitialization getInitialization() {
        return initialization;
    }

    // Setters
    public void setVisibility(Visibility visibility) {
        Validate.notNull(visibility);
        this.visibility = visibility;
    }

    public void setType(AbstractIdentifier type) {
        Validate.notNull(type);
        this.type = type;
    }

    public void setFieldName(AbstractIdentifier fieldName) {
        Validate.notNull(fieldName);
        this.fieldName = fieldName;
    }

    public void setInitialization(AbstractInitialization initialization) {
        Validate.notNull(initialization);
        this.initialization = initialization;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(visibility.toString().toLowerCase() + " ");
        type.decompile(s);
        s.print(" ");
        fieldName.decompile(s);
        initialization.decompile(s);
        s.println(";");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        fieldName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }

    @Override
    public void iterChildren(TreeFunction f) {
        type.iter(f);
        fieldName.iter(f);
        initialization.iter(f);
    }

    @Override
    protected void verifyDeclField(DecacCompiler compiler, ClassDefinition currentClass)
            throws ContextualError {
        // Todo
    }
}
