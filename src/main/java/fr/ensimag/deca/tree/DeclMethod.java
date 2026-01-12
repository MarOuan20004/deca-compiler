package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
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
    // on vérifie le type de retour de la methode
    Type mType = returnType.verifyType(compiler);
    // on vérifie les paramètres et on construit la signature
    Signature signature = new Signature();
    for (AbstractDeclParam typeParams : params.getList()) {
        DeclParam p = (DeclParam) typeParams;
        Type t = p.getType().verifyType(compiler);
        if (t==compiler.environmentType.VOID) {
            throw new ContextualError("le type VOID est interdit", p.getLocation());
        }
        signature.add(t);
    }
    Symbol mName = methodName.getName();
    EnvironmentExp membrs = currentClass.getMembers();
    ExpDefinition def = membrs.get(mName);

    // cas: nouvelle méthode
    if (def == null) {
        int i = currentClass.getNumberOfMethods();
        currentClass.incNumberOfMethods();
        MethodDefinition mDef = new MethodDefinition(mType, getLocation(), signature, i);
        methodName.setType(mType);
        methodName.setDefinition(mDef);
        try {
            membrs.declare(mName, mDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("methode deja definie", methodName.getLocation());
        }
    }
    // cas : redéfinition
    else {
        if (!(def instanceof MethodDefinition)) {
            throw new ContextualError("le nom existe deja mais ce n'es pas une methode",methodName.getLocation());
        }
        MethodDefinition oldDef = (MethodDefinition) def;
        // on vérifie la signature
        if (!signature.equals(oldDef.getSignature())) {
            throw new ContextualError("la methode heritee n'a pas la meme singature",methodName.getLocation());
        }
        // on vérifie le type de retour
        if (!mType.sameType(oldDef.getType())) {
            throw new ContextualError("le type de retour est incompatible", methodName.getLocation());
        }
        int i = oldDef.getIndex();
        MethodDefinition mDef =new MethodDefinition(mType, getLocation(), signature, i);
        methodName.setType(mType);
        methodName.setDefinition(mDef);
        try {
            membrs.declare(mName, mDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("methode deja defini", methodName.getLocation());
        }
    }
}

}
