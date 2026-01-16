package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * @author gl10
 * @date 01/01/2026
 */
public class DeclVar extends AbstractDeclVar {

    
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;

    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }

    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        // verify preconditions
        Validate.notNull(compiler);
        // verify the type
        Type t = type.verifyType(compiler);
        // verify the initialization
        initialization.verifyInitialization(compiler, t, localEnv, currentClass);
        if (t.isVoid()) {
            throw new ContextualError("Variable type cannot be void", type.getLocation());
        }
        VariableDefinition def = new VariableDefinition(t, varName.getLocation());
        try {
            // add the variable to the environment
            localEnv.declare(varName.getName(), def);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Variable already defined", varName.getLocation());
        }
        varName.setDefinition(def);
        
    }

    
    @Override
    public void decompile(IndentPrintStream s) {
        type.decompile(s);
        s.print(" ");
        varName.decompile(s);
        initialization.decompile(s);
        s.println(";");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }
    protected int codeGenDeclVar(DecacCompiler compiler, int currentOffset, Register base) {
        VariableDefinition def = (VariableDefinition) varName.getDefinition();
        // Chaque variable a son offset par rapport au registre de base
        def.setOperand(new RegisterOffset(currentOffset, base));

        // Calculer la valeur dans R0
        initialization.codeGenInitialization(compiler, Register.R0);

        // Stocker la valeur dans l'adresse mémoire de la variable
        compiler.addInstruction(new STORE(Register.R0, def.getOperand()));

        return currentOffset + 1; // prochain offset libre
    }
}