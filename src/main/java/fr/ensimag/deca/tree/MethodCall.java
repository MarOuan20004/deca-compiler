package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.ClassType;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;

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
            throw new ContextualError("Method call can only be applied to a class expression", operand.getLocation());
        }
        ClassDefinition classDef = ((ClassType) operandType).getDefinition();

        ExpDefinition def = classDef.getMembers().get(method.getName());

        if (def == null || !def.isMethod()) {
            throw new ContextualError("Identifier '" + method.getName().getName() + "' is not a method in class '" + classDef.getType().getName().getName() + "'", method.getLocation());
        }
        MethodDefinition methodDef = (MethodDefinition)def;

        if (arguments.size() != methodDef.getSignature().size()) {
            throw new ContextualError("Invalid number of arguments for method '" + method.getName().getName() + "'", getLocation());
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
    public int maxTemp() {
        // Empilements pour appel de méthode:
        // - ADDSP: arguments.size() + 1 (this + paramètres)
        // - BSR ajoute 2 mots supplémentaires (adresse de retour + ancien LB)
        return Math.max(operand.maxTemp(), arguments.size() + 3);
    }

    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        compiler.addInstruction(new ADDSP(arguments.size() + 1));
        operand.codeGenExpr(compiler, r);
        compiler.addInstruction(new STORE(r, new RegisterOffset(0, fr.ensimag.ima.pseudocode.Register.SP)));

        for (int i = 0; i < arguments.size(); i++) {
            AbstractExpr arg = arguments.getList().get(i);
            arg.codeGenExpr(compiler, r);
            compiler.addInstruction(new STORE(r, new RegisterOffset(-i - 1, fr.ensimag.ima.pseudocode.Register.SP)));
        }
        compiler.addInstruction(new LOAD(new RegisterOffset(0, fr.ensimag.ima.pseudocode.Register.SP), r));
        compiler.addInstruction(new CMP(new NullOperand(), r));
        compiler.addInstruction(new BEQ(new Label("dereference_null")));
        
        // Allouer un registre temporaire pour la table des méthodes
        GPRegister temp = compiler.getRegisterManager().allocateRegister();
        MethodDefinition methodDef = method.getMethodDefinition();
        
        if (temp != null) {
            // Si un registre temporaire est disponible, l'utiliser
            compiler.addInstruction(new LOAD(new RegisterOffset(0, r), temp));
            compiler.addInstruction(new BSR(new RegisterOffset(methodDef.getIndex(), temp)));
            compiler.getRegisterManager().freeRegister(temp);
        } else {
            // Sinon, utiliser la pile (mais cela ne devrait pas arriver)
            compiler.addInstruction(new LOAD(new RegisterOffset(0, r), r));
            compiler.addInstruction(new BSR(new RegisterOffset(methodDef.getIndex(), r)));
        }
        
        compiler.addInstruction(new SUBSP(arguments.size() + 1));
        
        if (methodDef.getType() != compiler.environmentType.VOID && r != fr.ensimag.ima.pseudocode.Register.R0) {
            compiler.addInstruction(new LOAD(fr.ensimag.ima.pseudocode.Register.R0, r));
        }
    }
}
