package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.ADDSP;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.SUBSP;
import fr.ensimag.ima.pseudocode.instructions.TSTO;

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

    public void codeGenMethodBody(DecacCompiler compiler) {
        int nbVar = declarations.getList().size();
        int maxTemps = instructions.maxTemp();
        int d = nbVar + maxTemps;

        compiler.addInstruction(new TSTO(d));
        compiler.addInstruction(new BOV(new fr.ensimag.ima.pseudocode.Label("stack_overflow_error")));
        compiler.addInstruction(new ADDSP(nbVar));
        compiler.addComment("Variables Declarations ");
        // Gestion des variables locales avec offset
        int offset = 1; // début du GB pour ce bloc
        for (AbstractDeclVar decl : declarations.getList()) {
            offset = ((DeclVar) decl).codeGenDeclVar(compiler, offset);
        }
        // traiter les instructions
        instructions.codeGenListInst(compiler);
        // libérer l'espace des variables locales
        compiler.addInstruction(new SUBSP(nbVar));
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
