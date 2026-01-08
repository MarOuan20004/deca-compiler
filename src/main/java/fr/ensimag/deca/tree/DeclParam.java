package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Declaration of a method parameter.
 *
 * @author gl10
 * @date 01/01/2026
 */
public class DeclParam extends AbstractDeclParam {
    private AbstractIdentifier type;
    private AbstractIdentifier paramName;

    public DeclParam(AbstractIdentifier type, AbstractIdentifier paramName) {
        Validate.notNull(type);
        Validate.notNull(paramName);
        this.type = type;
        this.paramName = paramName;
    }

    // Getters
    public AbstractIdentifier getType() {
        return type;
    }

    public AbstractIdentifier getParamName() {
        return paramName;
    }

    // Setters
    public void setType(AbstractIdentifier type) {
        Validate.notNull(type);
        this.type = type;
    }

    public void setParamName(AbstractIdentifier paramName) {
        Validate.notNull(paramName);
        this.paramName = paramName;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        paramName.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        paramName.prettyPrint(s, prefix, true   );
    }

    @Override
    public void iterChildren(TreeFunction f) {
        type.iter(f);
        paramName.iter(f);
    }
}
