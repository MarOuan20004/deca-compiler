package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.util.Set;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

/**
 * Declaration of a class method.
 *
 * @author gl10
 * @date 01/01/2026
 */
public class DeclMethod extends AbstractDeclMethod {
    private AbstractIdentifier returnType;
    private AbstractIdentifier methodName;
    private ListDeclParam params;
    private AbstractMethodBody body;
    private Visibility visibility;

    public DeclMethod(AbstractIdentifier returnType, AbstractIdentifier methodName,
            ListDeclParam params, AbstractMethodBody body, Visibility visibility) {
        Validate.notNull(returnType);
        Validate.notNull(methodName);
        Validate.notNull(params);
        Validate.notNull(body);
        Validate.notNull(visibility);
        this.returnType = returnType;
        this.methodName = methodName;
        this.params = params;
        this.body = body;
        this.visibility = visibility;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    // Getters
    public AbstractIdentifier getReturnType() {
        return returnType;
    }

    public AbstractIdentifier getMethodName() {
        return methodName;
    }

    public ListDeclParam getParams() {
        return params;
    }

    public AbstractMethodBody getBody() {
        return body;
    }

    // Setters
    public void setReturnType(AbstractIdentifier returnType) {
        Validate.notNull(returnType);
        this.returnType = returnType;
    }

    public void setMethodName(AbstractIdentifier methodName) {
        Validate.notNull(methodName);
        this.methodName = methodName;
    }

    public void setParams(ListDeclParam params) {
        Validate.notNull(params);
        this.params = params;
    }

    public void setBody(AbstractMethodBody body) {
        Validate.notNull(body);
        this.body = body;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        returnType.decompile(s);
        s.print(" ");
        methodName.decompile(s);
        s.print("(");
        params.decompile(s);
        s.print(") ");
        body.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        returnType.prettyPrint(s, prefix, false);
        methodName.prettyPrint(s, prefix, false);
        params.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }



    public void iterChildren(TreeFunction f) {
        returnType.iter(f);
        methodName.iter(f);
        params.iter(f);
        body.iter(f);
    }

   @Override
    protected void verifyDeclMethod(DecacCompiler compiler, ClassDefinition currentClass)
            throws ContextualError {
        Type mType = returnType.verifyType(compiler);
        Signature signature = new Signature();
        EnvironmentExp envParams = new EnvironmentExp(null); // Need an env for params
        for (AbstractDeclParam p : params.getList()) {
            p.verifyDeclParam(compiler, envParams);
            signature.add(((DeclParam)p).getType().getType());
        }

        Symbol mName = methodName.getName();
        MethodDefinition mDef;

        // Check for redefinition
        ExpDefinition superClassDef = currentClass.getSuperClass().getMembers().get(mName);
        if (superClassDef != null && superClassDef.isMethod()) {
            MethodDefinition superMethodDef = (MethodDefinition) superClassDef;
            // It's a redefinition, check for compatibility
            if (!mType.sameType(superMethodDef.getType())) {
                throw new ContextualError("Return type is incompatible with the overridden method", getLocation());
            }
            if (!signature.equals(superMethodDef.getSignature())) {
                throw new ContextualError("Signature is incompatible with the overridden method", getLocation());
            }
            mDef = new MethodDefinition(mType, getLocation(), signature, superMethodDef.getIndex(), visibility);
        } else {
            // It's a new method
            int methodIndex = currentClass.getNumberOfMethods() + 1;
            currentClass.incNumberOfMethods();
            mDef = new MethodDefinition(mType, getLocation(), signature, methodIndex, visibility);
        }
        
        try {
            currentClass.getMembers().declare(mName, mDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Method '" + mName.getName() + "' is already defined in this class", getLocation());
        }

        methodName.setDefinition(mDef);
        methodName.setType(mType);
    }
    void verifyDeclMethodBody(DecacCompiler compiler, ClassDefinition currentClass)
            throws ContextualError {
        MethodDefinition def =(MethodDefinition) methodName.getDefinition();
        EnvironmentExp envParams = new EnvironmentExp(null);
        params.verifyListDeclParam(compiler, envParams);
        body.verifyMethodBody(compiler,envParams,currentClass,def.getType());
        }

    @Override
    public void codeGenDeclMethod(DecacCompiler compiler) {
        compiler.getRegisterManager().beginMethodScope();

        // On génère d'abord le corps de la méthode pour savoir quels registres sont utilisés.
        // On stocke le corps dans un programme IMA temporaire.
        DecacCompiler tempCompiler = new DecacCompiler(compiler.getCompilerOptions(), compiler.getSource());
        tempCompiler.setRegisterManager(compiler.getRegisterManager());
        body.codeGenMethodBody(tempCompiler);

        // Prologue: sauvegarder les registres utilisés
        compiler.addComment("Sauvegarde des registres pour la méthode " + methodName.getName().getName());
        Set<GPRegister> usedRegisters = compiler.getRegisterManager().getUsedInCurrentMethod();
        for (GPRegister reg : usedRegisters) {
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.PUSH(reg));
        }
        
        // On ajoute le corps de la méthode au programme principal
        compiler.append(tempCompiler.getProgram());

        // Épilogue: restaurer les registres
        compiler.addComment("Restauration des registres pour la méthode " + methodName.getName().getName());
        // On utilise un stream pour inverser l'ordre facilement pour POP
        usedRegisters.stream()
            .sorted((r1, r2) -> Integer.compare(r2.getNumber(), r1.getNumber()))
            .forEach(reg -> compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.POP(reg)));
        
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.RTS());
    }
}
