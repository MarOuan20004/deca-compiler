package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.context.Definition ;
import fr.ensimag.deca.context.ClassType ;
import fr.ensimag.deca.context.MethodDefinition ;
import fr.ensimag.deca.context.ExpDefinition ;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;

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

    ExpDefinition def = classDef.getMembers().get(method.getName());

    if (def == null || !def.isMethod()) {
        throw new ContextualError("Selected identifier isn't a method of the class", method.getLocation());
    }
    MethodDefinition methodDef = def.asMethodDefinition("Selected identifier isn't a method of the class", method.getLocation());

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

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        // 1. Évaluer et empiler les arguments
        for (AbstractExpr arg : arguments.getList()) {
            arg.codeGenExpr(compiler, r);
            compiler.addInstruction(new PUSH(r));
        }
        
        // 2. Évaluer et empiler l'opérande
        operand.codeGenExpr(compiler, r);
        
        // Check if null
        compiler.addInstruction(new CMP(new NullOperand(), r));
        compiler.addInstruction(new BEQ(new Label("dereference_null")));

        compiler.addInstruction(new PUSH(r));
        
        // 3. Charger la VTable (l'objet est dans r)
        compiler.addInstruction(new LOAD(new RegisterOffset(0, r), r));
        
        // 4. Charger l'adresse de la méthode
        MethodDefinition methodDef = (MethodDefinition) method.getDefinition();
        int index = methodDef.getIndex();
        compiler.addInstruction(new LOAD(new RegisterOffset(index, r), r));
        
        // 5. Appeler la méthode
        compiler.addInstruction(new BSR(r));
        
        // 6. Dépiler this et les arguments
        compiler.addInstruction(new SUBSP(arguments.size() + 1));
        
        // 7. Le résultat est dans R0 par convention
        if (!getType().isVoid() && r != Register.R0) {
            compiler.addInstruction(new LOAD(Register.R0, r));
        }
    }
}
