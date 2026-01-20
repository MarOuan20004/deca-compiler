package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.*;
import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Deca complete program (class definition plus main block)
 *
 * @author gl10
 * @date 01/01/2026
 */
public class Program extends AbstractProgram {
    private static final Logger LOG = Logger.getLogger(Program.class);
    
    public Program(ListDeclFunction functions, ListDeclClass classes, AbstractMain main) {
        Validate.notNull(functions);
        Validate.notNull(classes);
        Validate.notNull(main);
        this.functions = functions;
        this.classes = classes;
        this.main = main;
    }
    public ListDeclFunction getFunctions() {
        return functions;
    }
    public ListDeclClass getClasses() {
        return classes;
    }
    public AbstractMain getMain() {
        return main;
    }
    private ListDeclFunction functions;
    private ListDeclClass classes;
    private AbstractMain main;

    @Override
    public void verifyProgram(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify program: start");
        //il faut qu'on vérifie les fonctions avant le reste
        functions.verifyListDeclFunction(compiler);
        //il faut qu'on vérifie les classes avant de vérifier le main 
        classes.verifyListClass(compiler);
        classes.verifyListClassMembers(compiler);
        
        functions.verifyListDeclFunctionBody(compiler);
        
        classes.verifyListClassBody(compiler);
        //Vérification du main
        main.verifyMain(compiler);
        LOG.debug("verify program: end");
    }

    @Override
    public void codeGenProgram(DecacCompiler compiler) {
        compiler.addComment("Construction of methods tables");
        int nextOffset = classes.codeGenMethTable(compiler);

        compiler.addComment("Main program");
        main.codeGenMain(compiler, nextOffset);

        compiler.addInstruction(new HALT());
        
        compiler.addComment("Global functions code generation");
        functions.codeGenListDeclFunction(compiler);

        compiler.addComment("Gestion des erreurs");
        compiler.addLabel(new Label("stack_overflow_error"));
        compiler.addInstruction(new WSTR("Error: Stack Overflow"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        compiler.addLabel(new Label("io_error"));
        compiler.addInstruction(new WSTR("Error: Input/Output error"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        compiler.addLabel(new Label("division_by_zero"));
        compiler.addInstruction(new WSTR("Error: Division by zero"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        compiler.addLabel(new Label("heap_overflow"));
        compiler.addInstruction(new WSTR("Error: Heap Overflow"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        compiler.addLabel(new Label("dereference_null"));
        compiler.addInstruction(new WSTR("Error: Dereference null"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        compiler.addLabel(new Label("float_arithmetic_overflow"));
        compiler.addInstruction(new WSTR("Error: float_Arithmetic overflow"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        compiler.addLabel(new Label("no_return_error"));
        compiler.addInstruction(new WSTR("Error: No return value "));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        compiler.addLabel(new Label("index_out_of_bounds"));
        compiler.addInstruction(new WSTR("Error: Index out of bounds"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());

        compiler.addComment("Classes code generation");
        classes.codeGenListClass(compiler);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        getFunctions().decompile(s);
        getClasses().decompile(s);
        getMain().decompile(s);
    }
    
    @Override
    protected void iterChildren(TreeFunction f) {
        functions.iter(f);
        classes.iter(f);
        main.iter(f);
    }
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        functions.prettyPrint(s, prefix, false);
        classes.prettyPrint(s, prefix, false);
        main.prettyPrint(s, prefix, true);
    }
}
