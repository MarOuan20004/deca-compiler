package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.FunctionDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;
import java.io.PrintStream;

public class FunctionCall extends AbstractExpr {
    private AbstractIdentifier functionName;
    private ListExpr arguments;

    public FunctionCall(AbstractIdentifier functionName, ListExpr arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        fr.ensimag.deca.context.ExpDefinition def = compiler.globalEnvExp.get(functionName.getName());
        if (def == null || !def.isFunction()) {
            throw new ContextualError("Function " + functionName.getName() + " undefined", getLocation());
        }
        FunctionDefinition functionDef = (FunctionDefinition) def;
        functionName.setDefinition(functionDef);
        
        Signature sig = functionDef.getSignature();
        if (sig.size() != arguments.size()) {
            throw new ContextualError("Function " + functionName.getName() + " expected " + sig.size() + " arguments, got " + arguments.size(), getLocation());
        }
        
        for (int i = 0; i < arguments.size(); i++) {
            arguments.getList().get(i).verifyRValue(compiler, localEnv, currentClass, sig.paramNumber(i));
        }
        
        setType(functionDef.getType());
        return functionDef.getType();
    }

    @Override
    public void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        // PUSH arguments
        compiler.addInstruction(new ADDSP(arguments.size()));
        int i = 0;
        for (AbstractExpr arg : arguments.getList()) {
            arg.codeGenExpr(compiler, r);
            compiler.addInstruction(new STORE(r, new fr.ensimag.ima.pseudocode.RegisterOffset(-i, fr.ensimag.ima.pseudocode.Register.SP)));
            i++;
        }
        
        compiler.addInstruction(new BSR(((FunctionDefinition)functionName.getDefinition()).getLabel()));
        compiler.addInstruction(new SUBSP(arguments.size()));
        compiler.addInstruction(new LOAD(fr.ensimag.ima.pseudocode.Register.R0, r));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        functionName.iter(f);
        arguments.iter(f);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        functionName.decompile(s);
        s.print("(");
        arguments.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        functionName.prettyPrint(s, prefix, false);
        arguments.prettyPrint(s, prefix, true);
    }
}
