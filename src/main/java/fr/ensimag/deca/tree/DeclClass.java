package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;

import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.util.HashMap;
import java.util.Map;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 * 
 * @author gl10
 * @date 01/01/2026
 */
public class DeclClass extends AbstractDeclClass {
    private AbstractIdentifier className;
    private AbstractIdentifier superClass;
    private ListDeclField fields;
    private ListDeclMethod methods;

    public DeclClass(AbstractIdentifier className,
            AbstractIdentifier superClass,
            ListDeclField fields,
            ListDeclMethod methods) {
        this.className = className;
        this.superClass = superClass;
        this.fields = fields;
        this.methods = methods;
    }

    public AbstractIdentifier getClassName() {
        return className;
    }
    public AbstractIdentifier getSuperClass() {
        return superClass;
    }
    public ListDeclField getFields() {
        return fields;
    }
    public ListDeclMethod getMethods() {
        return methods;
    }

    // etiquettes des méthodes de la classe
    private HashMap<Integer, Label> methodLabels = new HashMap<>();
    public HashMap<Integer, Label> getMethodLabels() {
        return methodLabels;
    }

    public void methodLabelsCreate(DecacCompiler compiler) {
        // methodes heritees , on cherche dans l'EnvExp
        ClassType superClassType = (ClassType) superClass.getType();
        if (superClassType != null) {
            ClassDefinition superClassDef = superClassType.getDefinition();
            EnvironmentExp superClassEnv = superClassDef.getMembers();
            for (Map.Entry<Symbol, ExpDefinition> entry : superClassEnv.getDictionary().entrySet()) {
                ExpDefinition methodDef = entry.getValue();
                if (methodDef.isMethod()) {
                    MethodDefinition superMethodDef = (MethodDefinition) methodDef;
                    String LabelName = "Code" + superClass.getName().getName()
                            + "." + entry.getKey().getName();
                    methodLabels.put(superMethodDef.getIndex(),
                            new Label(LabelName));
                }
            }
        }

        for (AbstractDeclMethod methodDecl : methods.getList()) {
            String methodLabelName = "Code" + className.getName().getName()
                    + "." + ((DeclMethod) methodDecl).getMethodName().getName().getName();
            MethodDefinition methodDef = (MethodDefinition)((DeclMethod) methodDecl)
                    .getMethodName().getDefinition();
            Label methodLabel = new Label(methodLabelName);
            methodLabels.put(methodDef.getIndex(), methodLabel);
        }
    }


    

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class { ... A FAIRE ... }");
    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }
    
    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        throw new UnsupportedOperationException("not yet implemented");
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        throw new UnsupportedOperationException("Not yet supported");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        throw new UnsupportedOperationException("Not yet supported");
    }

    @Override
    public void codeGenClass(DecacCompiler compiler) {
        // A FAIRE: compléter ce squelette très rudimentaire de code
        // code pour les champs et autres initialisations
        // code pour les méthodes de chaque classe
        
        
    }

    @Override
    public void codeGenMethTable(DecacCompiler compiler, int offset) {

        compiler.addComment("Code de la table des méthodes de la classe " + className.getName().getName());
        // use AdrTable 
        DAddr addr = getClassName().getClassDefinition().getAdrTable();
        compiler.addInstruction(new LEA(addr, Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(offset, Register.GB)));

        for (Map.Entry<Integer, Label> entry : methodLabels.entrySet()) {
            Label methodLabel = entry.getValue();
            int index= entry.getKey();
            compiler.addInstruction(new LOAD(new LabelOperand(methodLabel), Register.R0));
            compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(offset+index, Register.GB)));
            
        }
    }

}
