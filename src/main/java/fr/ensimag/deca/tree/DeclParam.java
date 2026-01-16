package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

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

    @Override
    protected void verifyDeclParam(DecacCompiler compiler, EnvironmentExp localEnv)
            throws ContextualError {
        Type paramType = type.verifyType(compiler);
        if (paramType==null) {
            throw new ContextualError("Type inconnue pour le paramètre", type.getLocation());
        }
        if (paramType.isVoid()) {
            throw new ContextualError("Le type d'un paramètre ne peut pas être void", type.getLocation());
        }
        if (getParamName()==null) {
            throw new ContextualError("Nom de paramètre null", paramName.getLocation());
        }
        ParamDefinition paramDef = new ParamDefinition(paramType, paramName.getLocation());
        try {
            localEnv.declare(paramName.getName(), paramDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Paramètre déjà défini", paramName.getLocation());
        }
        
    }
    
}
