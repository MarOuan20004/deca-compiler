package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import fr.ensimag.deca.tree.AbstractProgram;
import fr.ensimag.deca.tree.LocationException;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl10
 * @date 01/01/2026
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);
    
    public static void main(String[] args) {
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            options.displayUsage();
            System.exit(1);
        }
        if (options.getPrintBanner()) {
            System.out.println("Decac compiler - gl10");
            return;
        }
        if (options.getSourceFiles().isEmpty()) {
            throw new UnsupportedOperationException("decac without argument not yet implemented");
        }
        if (options.getParseOnly()) {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                try {
                    AbstractProgram ast = compiler.doLexingAndParsing(source.getPath(), System.err);
                    if (ast == null) {
                        LOG.error("Parsing failed for " + source.getName());
                        error = true;
                        continue;
                    }
                    ast.decompile(System.out);
                    
                } catch (Exception e) {
                    LOG.error("Error during parsing/decompilation: " + e.getMessage());
                    error = true;
                    }
            }
            System.exit(error ? 1 : 0);
        }
        if (options.getVerification()) {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                try {
                    AbstractProgram ast = compiler.doLexingAndParsing(source.getPath(), System.err);
                    if (ast == null) {
                        LOG.error("Parsing failed for " + source.getName());
                        error = true;
                        continue;
                    }
                    ast.verifyProgram(compiler);
                } catch (Exception e) {
                    if (e instanceof LocationException) {
                        ((LocationException) e).display(System.err);
                    } else {
                        LOG.error("Error during parsing/verification: " + e.getMessage());
                    }
                    error = true;
                }
            }
            System.exit(error ? 1 : 0);
        }
        if (options.getParallel()) {
            // A FAIRE : instancier DecacCompiler pour chaque fichier à
            // compiler, et lancer l'exécution des méthodes compile() de chaque
            // instance en parallèle. Il est conseillé d'utiliser
            // java.util.concurrent de la bibliothèque standard Java.
            ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors());
            
            for (File source : options.getSourceFiles()) {
                executor.submit(() -> {
                    DecacCompiler compiler = new DecacCompiler(options, source);
                    compiler.compile();
                });
            }
        } else {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }
        System.exit(error ? 1 : 0);
    }
}

