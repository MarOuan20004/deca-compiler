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
 * Expression "expr instanceof Type"
 */
public class InstanceOf extends AbstractExpr {
    private AbstractExpr expr;
    private AbstractIdentifier typeNom;

    public InstanceOf(AbstractExpr expr, AbstractIdentifier typeNom) {
        Validate.notNull(expr);
        Validate.notNull(typeNom);
        this.expr = expr;
        this.typeNom = typeNom;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        Type exprType = expr.verifyExpr(compiler, localEnv, currentClass);
        Type targetType = typeNom.verifyType(compiler);
        if (!exprType.isClassOrNull()) {
            throw new ContextualError("LOperand needs to be a null or class ", expr.getLocation());
        }
        if (!targetType.isClass()) {
            throw new ContextualError("ROperand of instanceof needs to be a class ", expr.getLocation());
        }
        this.setType(compiler.environmentType.BOOLEAN);
        return compiler.environmentType.BOOLEAN;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        expr.decompile(s);
        s.print(" instanceof ");
        typeNom.decompile(s);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        typeNom.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        typeNom.prettyPrint(s, prefix, true);
    }
}