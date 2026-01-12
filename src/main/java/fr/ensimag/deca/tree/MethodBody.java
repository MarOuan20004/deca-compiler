package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * Method body with declarations and instructions.
 *
 * @author gl10
 * @date 01/01/2026
 */
public class MethodBody extends AbstractMethodBody {
    private ListDeclVar declarations;
    private ListInst instructions;

    public MethodBody(ListDeclVar declarations, ListInst instructions) {
        Validate.notNull(declarations);
        Validate.notNull(instructions);
        this.declarations = declarations;
        this.instructions = instructions;
    }
    
    protected void verifyMethodBody(DecacCompiler compiler,EnvironmentExp envParams,ClassDefinition currentClass,Type returnType)
        throws ContextualError {
    EnvironmentExp localEnv = new EnvironmentExp(envParams);
    declarations.verifyListDeclVariable(compiler, localEnv, currentClass);
    instructions.verifyListInst(compiler,localEnv,currentClass,returnType);
}


    // Getters
    public ListDeclVar getDeclarations() {
        return declarations;
    }

    public ListInst getInstructions() {
        return instructions;
    }

    // Setters
    public void setDeclarations(ListDeclVar declarations) {
        Validate.notNull(declarations);
        this.declarations = declarations;
    }

    public void setInstructions(ListInst instructions) {
        Validate.notNull(instructions);
        this.instructions = instructions;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declarations.decompile(s);
        instructions.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declarations.prettyPrint(s, prefix, false);
        instructions.prettyPrint(s, prefix, true);
    }


    @Override
    public void iterChildren(TreeFunction f) {
        declarations.iter(f);
        instructions.iter(f);
    }

}
