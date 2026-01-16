package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentExp.DoubleDefException;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.STORE;

/**
 * Declaration of a class field.
 *
 * @author gl10
 * @date 01/01/2026
 */
public class DeclField extends AbstractDeclField {
    private Visibility visibility;
    private AbstractIdentifier type;
    private AbstractIdentifier fieldName;
    private AbstractInitialization initialization;

    public DeclField(Visibility visibility, AbstractIdentifier type,
            AbstractIdentifier fieldName, AbstractInitialization initialization) {
        Validate.notNull(visibility);
        Validate.notNull(type);
        Validate.notNull(fieldName);
        Validate.notNull(initialization);
        this.visibility = visibility;
        this.type = type;
        this.fieldName = fieldName;
        this.initialization = initialization;
    }

    // Getters
    public Visibility getVisibility() {
        return visibility;
    }

    public AbstractIdentifier getType() {
        return type;
    }

    public AbstractIdentifier getFieldName() {
        return fieldName;
    }

    public AbstractInitialization getInitialization() {
        return initialization;
    }

    // Setters
    public void setVisibility(Visibility visibility) {
        Validate.notNull(visibility);
        this.visibility = visibility;
    }

    public void setType(AbstractIdentifier type) {
        Validate.notNull(type);
        this.type = type;
    }

    public void setFieldName(AbstractIdentifier fieldName) {
        Validate.notNull(fieldName);
        this.fieldName = fieldName;
    }

    public void setInitialization(AbstractInitialization initialization) {
        Validate.notNull(initialization);
        this.initialization = initialization;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(visibility.toString().toLowerCase() + " ");
        type.decompile(s);
        s.print(" ");
        fieldName.decompile(s);
        initialization.decompile(s);
        s.println(";");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        fieldName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }

    @Override
    public void iterChildren(TreeFunction f) {
        type.iter(f);
        fieldName.iter(f);
        initialization.iter(f);
    }

    @Override
    protected void verifyDeclField(DecacCompiler compiler, ClassDefinition currentClass)
            throws ContextualError {
        Type typeField= type.verifyType(compiler);
        if(typeField == compiler.environmentType.VOID){
            throw new ContextualError("le type VOID est interdit ", currentClass.getLocation());
        }
        Symbol fieldNom  = fieldName.getName() ;
        EnvironmentExp envExp = currentClass.getMembers();
        ExpDefinition expDef = envExp.get(fieldNom) ;
        if(expDef !=null ){
            throw new ContextualError("field deja existant", currentClass.getLocation());
        }
        int i  = currentClass.getNumberOfFields() ;
        currentClass.incNumberOfFields() ; 
        FieldDefinition fieldDef = new FieldDefinition(typeField, currentClass.getLocation(), visibility, currentClass, i + 1);
        fieldName.setType(typeField);
        fieldName.setDefinition(fieldDef);
        try{
        envExp.declare(fieldNom, fieldDef);
        }
        catch(DoubleDefException e ){
            throw new ContextualError("Field deja existant", currentClass.getLocation());
        }
    }
    
    void verifyDeclFieldBody(DecacCompiler compiler, ClassDefinition currentClass)
        throws ContextualError {
        Type fieldType = fieldName.getType();
        EnvironmentExp env = currentClass.getMembers();
        initialization.verifyInitialization(compiler,fieldType,env,currentClass);
    }

    void codeGenInitField(DecacCompiler compiler, int index) {
        compiler.addInstruction(new LOAD(0,Register.R0));
        compiler.addInstruction(new LOAD(new RegisterOffset(-2,Register.LB), Register.R1));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(index, Register.R1)));
    }
    
    void codeGenDeclField(DecacCompiler compiler, int index) {
        if (getInitialization() instanceof NoInitialization) {
            // Pas d'initialisation, donc on met la valeur par défaut
            compiler.addInstruction(new LOAD(0, Register.R0));
        } else {
            // Générer le code pour l'initialisation
            getInitialization().codeGenInitialization(compiler, Register.R0);
        }
        // Charger l'adresse du champ dans R1
        compiler.addInstruction(new LOAD(new RegisterOffset(-2,Register.LB), Register.R1));
        // Stocker la valeur dans le champ de l'objet courant
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(index, Register.R1)));
    }

}
