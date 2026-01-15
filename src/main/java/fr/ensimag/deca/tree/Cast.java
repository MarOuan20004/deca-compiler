package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.deca.context.ClassType ;

/**
 * Cast expression (e.g. (int) x).
 *
 * @author gl10
 * @date 13/01/2026
 */

public class Cast extends AbstractExpr {

    private final AbstractIdentifier type;
    private final AbstractExpr expr;

    public Cast(AbstractIdentifier type, AbstractExpr expr) {
        Validate.notNull(type);
        Validate.notNull(expr);
        this.type = type;
        this.expr = expr;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
        Type targetType = type.verifyType(compiler);
        Type exprType = expr.verifyExpr(compiler, localEnv, currentClass);

        if (targetType.isVoid()) {
            throw new ContextualError("Cannot cast to void", getLocation());
        }

        if (exprType.sameType(targetType)) {
            // Casting to the same type is always allowed
            this.setType(targetType);
            return targetType;
        }

        if (exprType.isInt() && targetType.isFloat()) {
            this.setType(targetType);
            return targetType;
        }
        

        if (exprType.isClass() && targetType.isClass()) {
            ClassType exprClassType = ((ClassType)exprType).asClassType("", getLocation());
            ClassType targetClassType = ((ClassType)targetType).asClassType("", getLocation());
            // check subclass
            if (exprClassType.isSubClassOf(targetClassType)) {
                this.setType(targetType);
                return targetType;
            }
            // check superclass
            if (targetClassType.isSubClassOf(exprClassType)) {
                this.setType(targetType);
                return targetType;
            }
        }
        
        if (exprType.isNull() && targetType.isClass()) {
            this.setType(targetType);
            return targetType;
        }

        throw new ContextualError("Invalid cast from " + exprType.getName() + " to " + targetType.getName(), getLocation());
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        type.decompile(s);
        s.print(")(");
        expr.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        expr.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        expr.iter(f);
    }

}
