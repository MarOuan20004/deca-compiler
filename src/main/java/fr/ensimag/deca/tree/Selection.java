package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.Definition ;
import fr.ensimag.deca.context.ClassType ;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

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
  
}
