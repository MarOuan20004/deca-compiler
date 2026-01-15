package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

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
        if (superClass == null) {
            
        }
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

        int offset = methodLabels.size();
        RegisterOffset addr = new RegisterOffset(offset, Register.GB);
        getClassName().getClassDefinition().setAdrTable(addr);

        for (AbstractDeclMethod methodDecl : methods.getList()) {
            String methodLabelName = "Code" + className.getName().getName()
                    + "." + ((DeclMethod) methodDecl).getMethodName().getName().getName();
            MethodDefinition methodDef = (MethodDefinition)((DeclMethod) methodDecl)
                    .getMethodName().getDefinition();
            Label methodLabel = new Label(methodLabelName);
            methodLabels.put(methodDef.getIndex() + offset, methodLabel);
        }
    }


    

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class { ... A FAIRE ... }");
    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        System.out.println("Début de la vérification de la classe " + getClassName().getName().getName());
        TypeDefinition superClassTypeDefinition;

        if (superClass == null) {
            Symbol objectSymb = compiler.createSymbol("Object");
            superClassTypeDefinition = compiler.environmentType.defOfType(objectSymb);
            this.superClass = new Identifier(objectSymb);
            System.out.println("Classe racine détectée : extension implicite de Object");
            this.superClass.setDefinition(superClassTypeDefinition);
            this.superClass.setType(superClassTypeDefinition.getType());
        } else {
            Symbol SuperClassName = getSuperClass().getName();
            System.out.println("Vérification de la super-classe : " + SuperClassName.getName());
            superClassTypeDefinition = compiler.environmentType.defOfType(SuperClassName);
            if (superClassTypeDefinition == null) {
                throw new ContextualError("la classe n'existe pas dans EnvTypes", superClass.getLocation());
            }
            if (!superClassTypeDefinition.isClass()) {
                throw new ContextualError("Le symbole trouvé ne correspond pas à une classe", superClass.getLocation());
            }
            this.superClass.setDefinition(superClassTypeDefinition);
        }

        Symbol className = getClassName().getName();
        TypeDefinition classTypeDefinition = compiler.environmentType.defOfType(getClassName().getName());
        if (classTypeDefinition != null) {
            throw new ContextualError("La classe existe deja dans EnvTypes", getClassName().getLocation());
        }
        ClassType classType = new ClassType(className, getClassName().getLocation(), (ClassDefinition) superClassTypeDefinition);
        ClassDefinition classDefinition = classType.getDefinition();
        getClassName().setType(classType);
        System.out.println("Définition de la classe " + getClassName().getName().getName() + " créée avec succès.");
        getClassName().setDefinition(classDefinition);
        System.out.println("Definition " + classDefinition.toString() + " ajoutée au dictionnaire.");
        compiler.environmentType.envTypesAdd(className, classDefinition, getClassName().getLocation());
    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler) throws ContextualError {
        System.out.println("Vérification des membres de la classe " + getClassName().getName().getName());
        System.out.println("Vérification des membres de la classe " + getSuperClass().getName().getName());

        ClassDefinition classDefinition = getClassName().getClassDefinition() ;
        EnvironmentExp envExp = classDefinition.getMembers() ;
        classDefinition.setNumberOfFields(getSuperClass().getClassDefinition().getNumberOfFields());
        classDefinition.setNumberOfMethods(getSuperClass().getClassDefinition().getNumberOfMethods());
        fields.verifyListDeclField(compiler,classDefinition);
        methods.verifyListDeclMethod(compiler, classDefinition);
    }
    
    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        ClassDefinition currentClassDefinition = className.getClassDefinition() ;
        EnvironmentExp membrs = currentClassDefinition.getMembers() ;
        fields.verifyListDeclFieldBody(compiler, currentClassDefinition);
        methods.verifyListDeclMethodBody(compiler, currentClassDefinition);
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        className.prettyPrint(s, prefix, false);
        if (superClass != null) {
            superClass.prettyPrint(s, prefix, false);
        }
        fields.prettyPrint(s, prefix, false);
        methods.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        className.iter(f);
        superClass.iter(f);
        fields.iter(f);
        methods.iter(f);
    }

    @Override
    public void codeGenClass(DecacCompiler compiler) {
        // A FAIRE: compléter ce squelette très rudimentaire de code
        // code pour les champs et autres initialisations
        // code pour les méthodes de chaque classe
        
        
    }

    @Override
    public void codeGenMethTable(DecacCompiler compiler, int offset) {

        methodLabelsCreate(compiler);
        System.out.println("methodLabels: " + methodLabels);

        compiler.addComment("Code de la table des méthodes de la classe " + className.getName().getName());
        // use AdrTable 
        DAddr addr = getClassName().getClassDefinition().getAdrTable();
        System.out.println("adresse de la table des méthodes : "+addr);
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
