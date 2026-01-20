package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.util.Set;
import org.apache.log4j.Logger;

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
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.deca.context.ParamDefinition;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.POP;

/**
 * Declaration of a class method.
 *
 * @author gl10
 * @date 01/01/2026
 */
public class DeclMethod extends AbstractDeclMethod {
    private static final Logger LOG = Logger.getLogger(DeclMethod.class);
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
        if (visibility == Visibility.PROTECTED) {
            s.print("protected ");
        }
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
            LOG.debug("Method " + mName.getName() + " overrides method at " + superMethodDef.getLocation());
            mDef = new MethodDefinition(mType, getLocation(), signature, superMethodDef.getIndex());
        } else {
            // It's a new method
            int methodIndex = currentClass.getNumberOfMethods() + 1;
            currentClass.incNumberOfMethods();
            LOG.debug("Method " + mName.getName() + " defined at " + getLocation());
            mDef = new MethodDefinition(mType, getLocation(), signature, methodIndex);
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
        
        // Set operands for parameters
        int n = params.size();
        for (int i = 0; i < n; i++) {
            AbstractDeclParam p = params.getList().get(i);
            DeclParam dp = (DeclParam) p;
            ParamDefinition pDef = (ParamDefinition) envParams.get(dp.getParamName().getName());
            pDef.setOperand(new RegisterOffset(-(i + 3), Register.LB));
        }
        
        body.verifyMethodBody(compiler,envParams,currentClass,def.getType());
    }

    @Override
    public void codeGenDeclMethod(DecacCompiler compiler, ClassDefinition currentClass) {
        compiler.getRegisterManager().beginMethodScope();

        // Create end label
        String className = currentClass.getType().getName().getName();
        String methodLabelName = methodName.getName().getName();
        Label endLabel = new Label("fin." + className + "." + methodLabelName);
        compiler.setCurrentMethodEndLabel(endLabel);

        // On génère d'abord le corps de la méthode pour savoir quels registres sont utilisés.
        // On stocke le corps dans un programme IMA temporaire.
        DecacCompiler tempCompiler = new DecacCompiler(compiler.getCompilerOptions(), compiler.getSource());
        tempCompiler.setRegisterManager(compiler.getRegisterManager());
        tempCompiler.setCurrentMethodEndLabel(endLabel);
        body.codeGenMethodBody(tempCompiler);

        //sauvegarder les registres utilisés
        compiler.addComment("Sauvegarde des registres");
        Set<GPRegister> usedRegisters = compiler.getRegisterManager().getUsedInCurrentMethod();
        int nbSavedRegisters = usedRegisters.size();
        
        // On génère le corps de nouveau avec le bon nombre de registres sauvegardés
        tempCompiler = new DecacCompiler(compiler.getCompilerOptions(), compiler.getSource());
        tempCompiler.setRegisterManager(compiler.getRegisterManager());
        tempCompiler.setCurrentMethodEndLabel(endLabel);
        body.codeGenMethodBody(tempCompiler, nbSavedRegisters);
        
        for (GPRegister reg : usedRegisters) {
            compiler.addInstruction(new PUSH(reg));
        }   
        // On ajoute le corps de la méthode au programme principal
        compiler.append(tempCompiler.getProgram());

        if (!returnType.getDefinition().getType().isVoid()) {
            compiler.addInstruction(new BRA(new Label("no_return_error")));
        }

        // Étiquette de fin
        compiler.addLabel(endLabel);
        // restaurer les registres
        compiler.addComment("Restauration des registres  " );
        // On utilise un stream pour inverser l'ordre facilement pour POP
        usedRegisters.stream()
            .sorted((r1, r2) -> Integer.compare(r2.getNumber(), r1.getNumber()))
            .forEach(reg -> compiler.addInstruction(new POP(reg)));
        
        compiler.addInstruction(new RTS());
        
        compiler.setCurrentMethodEndLabel(null);
    }
}