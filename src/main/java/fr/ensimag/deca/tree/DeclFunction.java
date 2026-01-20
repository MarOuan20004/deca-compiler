package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.commons.lang.Validate;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FunctionDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.POP;

public class DeclFunction extends AbstractDeclFunction {
    private static final Logger LOG = Logger.getLogger(DeclFunction.class);
    private AbstractIdentifier returnType;
    private AbstractIdentifier functionName;
    private ListDeclParam params;
    private AbstractMethodBody body;

    public DeclFunction(AbstractIdentifier returnType, AbstractIdentifier functionName,
            ListDeclParam params, AbstractMethodBody body) {
        Validate.notNull(returnType);
        Validate.notNull(functionName);
        Validate.notNull(params);
        Validate.notNull(body);
        this.returnType = returnType;
        this.functionName = functionName;
        this.params = params;
        this.body = body;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        returnType.decompile(s);
        s.print(" ");
        functionName.decompile(s);
        s.print("(");
        params.decompile(s);
        s.print(") ");
        body.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        returnType.prettyPrint(s, prefix, false);
        functionName.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        returnType.iter(f);
        functionName.iter(f);
        params.iter(f);
        body.iter(f);
    }

    @Override
    protected void verifyDeclFunction(DecacCompiler compiler) throws ContextualError {
        Type type = returnType.verifyType(compiler);
        Signature signature = new Signature();
        EnvironmentExp envParams = new EnvironmentExp(compiler.globalEnvExp);
        for (AbstractDeclParam p : params.getList()) {
            p.verifyDeclParam(compiler, envParams);
            signature.add(((DeclParam)p).getType().getType());
        }
        FunctionDefinition def = new FunctionDefinition(type, getLocation(), signature);
        def.setLabel(new Label("function." + functionName.getName().getName()));
        try {
            compiler.globalEnvExp.declare(functionName.getName(), def);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Function " + functionName.getName() + " already defined", getLocation());
        }
        functionName.setDefinition(def);
        functionName.setType(type);
    }

    @Override
    protected void verifyDeclFunctionBody(DecacCompiler compiler) throws ContextualError {
        EnvironmentExp envParams = new EnvironmentExp(compiler.globalEnvExp);
        params.verifyListDeclParam(compiler, envParams);
        
        int n = params.size();
        for (int i = 0; i < n; i++) {
            AbstractDeclParam p = params.getList().get(i);
            DeclParam dp = (DeclParam) p;
            ParamDefinition pDef = (ParamDefinition) envParams.get(dp.getParamName().getName());
            pDef.setOperand(new RegisterOffset(-(i + 2), Register.LB));
        }
        
        body.verifyMethodBody(compiler, envParams, null, returnType.getType());
    }

    @Override
    protected void codeGenDeclFunction(DecacCompiler compiler) {
        compiler.getRegisterManager().beginMethodScope();
        Label endLabel = new Label("fin.function." + functionName.getName().getName());
        compiler.setCurrentMethodEndLabel(endLabel);

        compiler.addLabel(((FunctionDefinition)functionName.getDefinition()).getLabel());

        DecacCompiler tempCompiler = new DecacCompiler(compiler.getCompilerOptions(), compiler.getSource());
        tempCompiler.setRegisterManager(compiler.getRegisterManager());
        tempCompiler.setCurrentMethodEndLabel(endLabel);
        body.codeGenMethodBody(tempCompiler);

        Set<GPRegister> usedRegisters = compiler.getRegisterManager().getUsedInCurrentMethod();
        int nbSavedRegisters = usedRegisters.size();

        tempCompiler = new DecacCompiler(compiler.getCompilerOptions(), compiler.getSource());
        tempCompiler.setRegisterManager(compiler.getRegisterManager());
        tempCompiler.setCurrentMethodEndLabel(endLabel);
        body.codeGenMethodBody(tempCompiler, nbSavedRegisters);

        for (GPRegister reg : usedRegisters) {
            compiler.addInstruction(new PUSH(reg));
        }
        compiler.append(tempCompiler.getProgram());

        if (!returnType.getType().isVoid()) {
            compiler.addInstruction(new BRA(new Label("no_return_error")));
        }

        compiler.addLabel(endLabel);
        usedRegisters.stream()
            .sorted((r1, r2) -> Integer.compare(r2.getNumber(), r1.getNumber()))
            .forEach(reg -> compiler.addInstruction(new POP(reg)));
        
        compiler.addInstruction(new RTS());
        compiler.setCurrentMethodEndLabel(null);
    }
}
