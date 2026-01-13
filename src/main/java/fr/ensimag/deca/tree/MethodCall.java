package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Method call (e.g. x.y(a, b))
 *
 * @author gl10
 * @date 01/01/2026
 */
public class MethodCall extends AbstractExpr {
    private AbstractExpr operand;
    private AbstractIdentifier method;
    private ListExpr arguments;

    public MethodCall(AbstractExpr operand, AbstractIdentifier method, ListExpr arguments) {
        Validate.notNull(operand);
        Validate.notNull(method);
        Validate.notNull(arguments);
        this.operand = operand;
        this.method = method;
        this.arguments = arguments;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void decompile(IndentPrintStream s) {
        operand.decompile(s);
        s.print(".");
        method.decompile(s);
        s.print("(");
        arguments.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        operand.prettyPrint(s, prefix, false);
        method.prettyPrint(s, prefix, false);
        arguments.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        operand.iter(f);
        method.iter(f);
        arguments.iter(f);
    }
}
