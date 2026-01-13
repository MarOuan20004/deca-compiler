package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Expression "new Identifier()"
 *
 * @author Ensimag
 * @date 2026
 */
public class New extends AbstractExpr {
    private AbstractIdentifier typeName;

    public New(AbstractIdentifier typeName) {
        Validate.notNull(typeName);
        this.typeName = typeName;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        // 1. On vérifie que l'identificateur correspond bien à un type existant
        Type type = typeName.verifyType(compiler);
        
        // 2. On vérifie que ce type est bien une classe (on ne peut pas faire new int())
        if (!type.isClass()) {
            throw new ContextualError("Type '" + typeName.getName() + "' is not a class.",
                    getLocation());
        }
        
        // On fixe le type de l'expression New
        this.setType(type);
        return type;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        typeName.decompile(s);
        s.print("()");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        typeName.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        typeName.iter(f);
    }
}