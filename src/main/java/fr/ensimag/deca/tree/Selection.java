package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp ;
import fr.ensimag.deca.context.FieldDefinition ;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 * Field selection (e.g. x.y)
 *
 * @author gl10
 * @date 13/01/2026
 */
public class Selection extends AbstractLValue {
    private AbstractExpr operand;
    private AbstractIdentifier field;

    public Selection(AbstractExpr operand, AbstractIdentifier field) {
        Validate.notNull(operand);
        Validate.notNull(field);
        this.operand = operand;
        this.field = field;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        Type operandType = operand.verifyExpr(compiler, localEnv, currentClass);

        if (!operandType.isClass()) {
            throw new ContextualError("Selection only applied to a class expression",operand.getLocation());
        }
        ClassDefinition classDef =((ClassType) operandType).getDefinition();

        Definition definition =classDef.getMembers().get(field.getName());

        if (definition == null || !definition.isField()) {
            throw new ContextualError("the ident isn't a field of the class",field.getLocation());
        }
        FieldDefinition fieldDef = (FieldDefinition) definition;

        if (fieldDef.getVisibility() == Visibility.PROTECTED) {
            if (currentClass == null) {
                throw new ContextualError("Protected field isn't accessible ",field.getLocation());
            }
            boolean accessible = false;
            ClassDefinition c = currentClass;
            while (c != null) {
                if (c == fieldDef.getContainingClass()) {
                    accessible = true;
                    break;
                }
                c = c.getSuperClass();
            }
            if (!accessible) {
                throw new ContextualError("Protected field isn't accessible ",field.getLocation());
            }
        }
        field.setDefinition(fieldDef);
        field.setType(fieldDef.getType());
        this.setType(fieldDef.getType());
        return fieldDef.getType();
    }

    


    @Override
    public void decompile(IndentPrintStream s) {
        operand.decompile(s);
        s.print(".");
        field.decompile(s);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        operand.prettyPrint(s, prefix, false);
        field.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        operand.iter(f);
        field.iter(f);
    }
    @Override
    protected void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        // 1. Évaluer l'opérande (l'objet)
        operand.codeGenExpr(compiler, r);
        
        // Check if null
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.CMP(new fr.ensimag.ima.pseudocode.NullOperand(), r));
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BEQ(new fr.ensimag.ima.pseudocode.Label("dereference_null")));

        // 2. Récupérer la définition du champ pour avoir son offset
        FieldDefinition fieldDef = (FieldDefinition) field.getDefinition();
        int offset = fieldDef.getIndex();
        
        // 3. Charger la valeur du champ
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(new fr.ensimag.ima.pseudocode.RegisterOffset(offset, r), r));
    }

    protected void codeGenAssign(DecacCompiler compiler, GPRegister valueReg) {
        // 1. Évaluer l'opérande (l'objet)
        GPRegister objectReg = compiler.getRegisterManager().allocateRegister();
        operand.codeGenExpr(compiler, objectReg);
        
        // Check if null
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.CMP(new fr.ensimag.ima.pseudocode.NullOperand(), objectReg));
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BEQ(new fr.ensimag.ima.pseudocode.Label("dereference_null")));

        // 2. Récupérer la définition du champ pour avoir son offset
        FieldDefinition fieldDef = (FieldDefinition) field.getDefinition();
        int offset = fieldDef.getIndex();
        
        // 3. Stocker la valeur dans le champ : Mem[obj + offset] = valueReg
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.STORE(valueReg, new fr.ensimag.ima.pseudocode.RegisterOffset(offset, objectReg)));
        
        compiler.getRegisterManager().freeRegister(objectReg);
    }
}
