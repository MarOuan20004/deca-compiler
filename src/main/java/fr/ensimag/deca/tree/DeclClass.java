package fr.ensimag.deca.tree;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUB;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 * 
 * @author gl10
 * @date 01/01/2026
 */
public class DeclClass extends AbstractDeclClass {
    private static final Logger LOG = Logger.getLogger(DeclClass.class);
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
        // For each method, find its index and put the method label at this index
        ClassType superClassType = (ClassType) superClass.getType();
        // Add inherited methods first (recursively)
        if (superClassType != null) {
            ClassDefinition superClassDef = superClassType.getDefinition();
            addInheritedMethodsToLabels(superClassDef);
        }
        
        // Add or replace methods of the current class
        // Each method goes at its own index from MethodDefinition
        for (AbstractDeclMethod methodDecl : methods.getList()) {
            DeclMethod declMethod = (DeclMethod) methodDecl;
            String methodName = declMethod.getMethodName().getName().getName();
            String methodLabelFullName = "Code." + className.getName().getName() + "." + methodName;
            Label methodLabel = new Label(methodLabelFullName);
            MethodDefinition methodDef = (MethodDefinition) declMethod.getMethodName().getDefinition();
            int methodOwnIndex = methodDef.getIndex();           
            // Put the label at this method's index
            // If it's overriding, this replaces the parent's label at the same index
            methodLabels.put(methodOwnIndex, methodLabel);
        }
        getClassName().getClassDefinition().setNumberOfMethods(methodLabels.size());
    }
    
    private void addInheritedMethodsToLabels(ClassDefinition superClassDef) {
        // Recursively add inherited methods by their own indices
        ClassDefinition superOfSuper = superClassDef.getSuperClass();
        if (superOfSuper != null) {
            addInheritedMethodsToLabels(superOfSuper);
        }
        EnvironmentExp env = superClassDef.getMembers();
        String superClassName = superClassDef.getType().getName().getName();
        
        for (Map.Entry<Symbol, ExpDefinition> entry : env.getDictionary().entrySet()) {
            if (entry.getValue().isMethod()) {
                MethodDefinition methodDef = (MethodDefinition) entry.getValue();
                String methodName = entry.getKey().getName();
                String methodLabelName = "Code." + superClassName + "." + methodName;
                Label methodLabel = new Label(methodLabelName);
                // Put the label at the method's own index
                methodLabels.put(methodDef.getIndex(), methodLabel);
            }
        }
    }


    

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("class { ... A FAIRE ... }");
    }

    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        LOG.debug("Début de la vérification de la classe " + getClassName().getName().getName());
        TypeDefinition superClassTypeDefinition;

        if (superClass == null) {
            Symbol objectSymb = compiler.createSymbol("Object");
            superClassTypeDefinition = compiler.environmentType.defOfType(objectSymb);
            this.superClass = new Identifier(objectSymb);
            LOG.debug("Classe racine détectée : extension implicite de Object");
            this.superClass.setDefinition(superClassTypeDefinition);
            this.superClass.setType(superClassTypeDefinition.getType());
        } else {
            Symbol SuperClassName = getSuperClass().getName();
            LOG.debug("Vérification de la super-classe : " + SuperClassName.getName());
            superClassTypeDefinition = compiler.environmentType.defOfType(SuperClassName);
            if (superClassTypeDefinition == null) {
                throw new ContextualError("la classe n'existe pas dans EnvTypes", superClass.getLocation());
            }
            if (!superClassTypeDefinition.isClass()) {
                throw new ContextualError("Le symbole trouvé ne correspond pas à une classe", superClass.getLocation());
            }
            this.superClass.setDefinition(superClassTypeDefinition);
            this.superClass.setType(superClassTypeDefinition.getType());
        }

        Symbol className = getClassName().getName();
        TypeDefinition classTypeDefinition = compiler.environmentType.defOfType(getClassName().getName());
        if (classTypeDefinition != null) {
            throw new ContextualError("La classe existe deja dans EnvTypes", getClassName().getLocation());
        }
        ClassType classType = new ClassType(className, getClassName().getLocation(), (ClassDefinition) superClassTypeDefinition);
        ClassDefinition classDefinition = classType.getDefinition();
        getClassName().setType(classType);
        LOG.debug("Définition de la classe " + getClassName().getName().getName() + " créée avec succès.");
        getClassName().setDefinition(classDefinition);
        LOG.debug("Definition " + classDefinition.toString() + " ajoutée au dictionnaire.");
        compiler.environmentType.envTypesAdd(className, classDefinition, getClassName().getLocation());
    }

    @Override
    protected void verifyClassMembers(DecacCompiler compiler) throws ContextualError {
        LOG.debug("Vérification des membres de la classe " + getClassName().getName().getName());
        LOG.debug("Vérification des membres de la classe " + getSuperClass().getName().getName());

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
        // 1-initialiser à 0
        compiler.addLabel(new Label("init."+className.getName().getName()));
        if (!superClass.getName().getName().equals("Object")) {
            // 2- Appeler le constructeur par defaut de la super classe
            callConstructorOfSuperClass(compiler);
            fields.codeGenListDeclFieldInit(compiler);
        }
        // 3-Intisialisation explicite
        fields.codeGenListDeclField(compiler);
        compiler.addInstruction(new RTS());
        // 4-Code des méthodes
        methods.codeGenListDeclMethod(compiler, className.getClassDefinition());
    }

    private void callConstructorOfSuperClass(DecacCompiler compiler) {
        ClassType superClassType = (ClassType) superClass.getType();
        if (superClassType != null && !superClassType.getName().getName().equals("Object")) {
            ClassDefinition superClassDef = superClassType.getDefinition();
            String superClassName = superClassDef.getType().getName().getName();
            String constructorLabelName = "init." + superClassName;
            Label constructorLabel = new Label(constructorLabelName);
            // Appeler le constructeur de la super-classe
            compiler.addComment("Appel du constructeur de la super-classe " + superClassName);
            // Préparer l'appel en empilant l'adresse de l'objet courant (this)
            // On charge "this" (paramètre -2(LB)) dans R1 avant de le pusher
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
            compiler.addInstruction(new PUSH(Register.R1));
            // Appeler le constructeur
            compiler.addInstruction(new BSR(constructorLabel));
            // Nettoyer la pile après l'appel
            compiler.addInstruction(new SUBSP(1));
            
        }
    }

    @Override
    public void codeGenMethTable(DecacCompiler compiler, int offset) {

        methodLabelsCreate(compiler);

        compiler.addComment("Code de la table des méthodes de la classe " + className.getName().getName());
        
        // Setter l'adresse de la table dans la ClassDefinition
        RegisterOffset addr = new RegisterOffset(offset, Register.GB);
        getClassName().getClassDefinition().setAdrTable(addr);
        
        // Stocker l'adresse de la table de la super-classe
        DAddr superTableAddr = null;
        ClassType superClassType = (ClassType) superClass.getType();
        if (superClassType != null) {
            ClassDefinition superClassDef = superClassType.getDefinition();
            superTableAddr = superClassDef.getAdrTable();
        }
        
        if (superTableAddr != null) {
            compiler.addInstruction(new LEA(superTableAddr, Register.R0));
        } else {
            // Si pas de super-classe (Object), stocker null
            compiler.addInstruction(new LOAD(new fr.ensimag.ima.pseudocode.NullOperand(), Register.R0));
        }
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(offset, Register.GB)));

        for (Map.Entry<Integer, Label> entry : methodLabels.entrySet()) {
            Label methodLabel = entry.getValue();
            compiler.addInstruction(new LOAD(new LabelOperand(methodLabel), Register.R0));
            compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(offset + entry.getKey(), Register.GB)));
        }
    }

}
