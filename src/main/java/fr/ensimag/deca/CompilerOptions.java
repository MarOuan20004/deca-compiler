package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl10
 * @date 01/01/2026
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;
    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }
    
    public boolean getParseOnly() {
        return parseOnly;
    }
    
    public boolean getVerification() {
        return verification;
    }
    
    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    private int debug = 0;
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean parseOnly = false;
    private boolean verification = false;
    private List<File> sourceFiles = new ArrayList<File>();

    
    public void parseArgs(String[] args) throws CLIException {
        for (int i=0; i < args.length; i++) {
            String arg = args[i];
            if ("-v".equals(arg)) {
                verification = true;
            } else if ("-d".equals(arg)) {
                debug++;
            } else if ("-p".equals(arg)) {
                parseOnly = true;
            }
            else if ("-P".equals(arg)) {
                parallel = true;
            } else if ("-b".equals(arg)) {
                printBanner = true;
            } else if ("-r".equals(arg)) {
                if (i + 1 >= args.length) {
                    throw new CLIException("Option -r nécessite un argument");
                }
                i++;
                try {
                    int n = Integer.parseInt(args[i]);
                    if (n < 4 || n > 16) {
                        throw new CLIException("Le nombre de registres doit être entre 4 et 16");
                    }
                    // Configurer le gestionnaire de registres avec n registres
                    RegisterManager registerManager = new RegisterManager();
                    registerManager.setNbrRegistresMax(n);
                } catch (NumberFormatException e) {
                    throw new CLIException("Argument invalide pour l'option -r : " + args[i]);
                }
            } else if (arg.startsWith("-")) {
                throw new CLIException("Option non reconnue : " + arg);
            } else {
                sourceFiles.add(new File(arg));
            }
        }
        Logger logger = Logger.getRootLogger();
        switch (getDebug()) {
        case QUIET: break;
        case INFO:
            logger.setLevel(Level.INFO); break;
        case DEBUG:
            logger.setLevel(Level.DEBUG); break;
        case TRACE:
            logger.setLevel(Level.TRACE); break;
        default:
            logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());
        boolean assertsEnabled = false;
        assert assertsEnabled = true;
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }
    }

    protected void displayUsage() {
        PrintStream err = System.err;
        err.println("usage: decac [-b] [-v] [-d]* [-P] <source files>");
    }
}
