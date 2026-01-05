package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.instructions.TSTO;

import java.io.PrintStream;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * @author gl10
 * @date 01/01/2026
 */
public class Main extends AbstractMain {
    private static final Logger LOG = Logger.getLogger(Main.class);
    
    private ListDeclVar declVariables;
    private ListInst insts;
    public Main(ListDeclVar declVariables,
            ListInst insts) {
        Validate.notNull(declVariables);
        Validate.notNull(insts);
        this.declVariables = declVariables;
        this.insts = insts;
    }

    @Override
    protected void verifyMain(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify Main: start");
        EnvironmentExp localEnv = new EnvironmentExp(null);
        declVariables.verifyListDeclVariable(compiler, localEnv, null);
        insts.verifyListInst(compiler, localEnv, null, compiler.environmentType.VOID);
        LOG.debug("verify Main: end");
    }

    @Override
    protected void codeGenMain(DecacCompiler compiler) {
        // traiter les déclarations de variables.
        //  calculer le nombre de variables
        int nbVar = declVariables.getList().size();

        // calculer le nombre maximal de temporaires
        int maxTemps = insts.maxTemp(); // implémenter maxTemp() dans ListInst

        int d = nbVar + maxTemps;

        compiler.addInstruction(new TSTO(d));
        
        compiler.addComment("Beginning of main instructions:");
        // Gestion des variables locales avec offset
        int offset = 0; // début du GB pour ce bloc
        for (AbstractDeclVar decl : declVariables.getList()) {
            offset = ((DeclVar) decl).codeGenDeclVar(compiler, offset);
        }
        insts.codeGenListInst(compiler);
    }
    
    @Override
    public void decompile(IndentPrintStream s) {
        s.println("{");
        s.indent();
        declVariables.decompile(s);
        insts.decompile(s);
        s.unindent();
        s.println("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        declVariables.iter(f);
        insts.iter(f);
    }
 
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        declVariables.prettyPrint(s, prefix, false);
        insts.prettyPrint(s, prefix, true);
    }
}
