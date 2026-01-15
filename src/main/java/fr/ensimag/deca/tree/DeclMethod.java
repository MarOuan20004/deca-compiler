package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.tree.MethodBody;
import fr.ensimag.deca.context.Signature;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

import static org.mockito.Mockito.never;

import java.io.PrintStream;

import org.antlr.v4.runtime.atn.SemanticContext.OR;
import org.apache.commons.lang.Validate;

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

    public DeclMethod(AbstractIdentifier returnType, AbstractIdentifier methodName,
            ListDeclParam params, AbstractMethodBody body) {
        Validate.notNull(returnType);
        Validate.notNull(methodName);
        Validate.notNull(params);
        Validate.notNull(body);
        this.returnType = returnType;
        this.methodName = methodName;
        this.params = params;
        this.body = body;
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
    
    // 1. Types et Signature
    Type mType = returnType.verifyType(compiler);
    Signature signature = new Signature();
    for (AbstractDeclParam p : params.getList()) {
        Type pType =((DeclParam) p).getType().verifyType(compiler); // Utilise la méthode de l'objet p directement
        if (pType.isVoid()) {
            throw new ContextualError("Le type void est interdit pour un paramètre", p.getLocation());
        }
        signature.add(pType);
    }

    Symbol mName = methodName.getName();
    
    // 2. Recherche de la définition dans la SUPER CLASSE uniquement pour l'override
    MethodDefinition superDef = null;
    if (currentClass.getSuperClass() != null) {
        ExpDefinition defInSuper = currentClass.getSuperClass().getMembers().get(mName);
        if (defInSuper != null && defInSuper.isMethod()) {
            superDef = (MethodDefinition) defInSuper;
        } else if (defInSuper != null && defInSuper.isField()) {
            throw new ContextualError("Le nom " + mName + " est déjà utilisé par un champ dans une super-classe", getLocation());
        }
    }

    MethodDefinition mDef;
    if (superDef == null) {
        // CAS : Nouvelle méthode
        int index = currentClass.getNumberOfMethods();
        mDef = new MethodDefinition(mType, getLocation(), signature, index);
        currentClass.incNumberOfMethods();
    } else {
        // CAS : Redéfinition (Override)
        // Vérification de la signature (doit être identique)
        if (!signature.equals(superDef.getSignature())) {
            throw new ContextualError("Redéfinition impossible : signature différente", getLocation());
        }
        // Vérification du type de retour (doit être compatible/identique)
        if (!mType.sameType(superDef.getType())) { // Ou isSubtypeOf selon ton avancement
            throw new ContextualError("Redéfinition impossible : type de retour incompatible", getLocation());
        }
        
        // On garde le MÊME index que la méthode héritée pour la VTable
        mDef = new MethodDefinition(mType, getLocation(), signature, superDef.getIndex());
    }

    // 3. ON DÉCLARE DANS LA CLASSE COURANTE
    try {
        currentClass.getMembers().declare(mName, mDef);
    } catch (EnvironmentExp.DoubleDefException e) {
        throw new ContextualError("Méthode " + mName + " déjà définie dans cette classe", methodName.getLocation());
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


}
