package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.Definition ;
import fr.ensimag.deca.context.ClassType ;
import fr.ensimag.deca.context.MethodDefinition ;
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

    Type operandType = operand.verifyExpr(compiler, localEnv, currentClass);

    if (!operandType.isClass()) {
        throw new ContextualError("Method call only applied to a class expression",operand.getLocation());
    }
    ClassDefinition classDef =((ClassType) operandType).getDefinition();

    Definition def =classDef.getMembers().get(method.getName());

    if (def == null || !def.isMethod()) {
        throw new ContextualError("Selected identifier isn't a method of the class", method.getLocation());
    }
    MethodDefinition methodDef = (MethodDefinition) def;

    if (arguments.size() != methodDef.getSignature().size()) {
        throw new ContextualError( "th number of arguments in method call is false",getLocation());
    }
    for (int i = 0; i < arguments.size(); i++) {
        AbstractExpr arg = arguments.getList().get(i);
        Type paramType = methodDef.getSignature().paramNumber(i);

        arg.verifyRValue(compiler, localEnv, currentClass, paramType);
    }

    method.setDefinition(methodDef);
    method.setType(methodDef.getType());
    this.setType(methodDef.getType());
    return methodDef.getType();
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
