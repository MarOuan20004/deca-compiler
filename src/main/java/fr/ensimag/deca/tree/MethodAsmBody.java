package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

/**
 * Assembly language method body.
 *
 * @author gl10
 * @date 01/01/2026
 */
public class MethodAsmBody extends AbstractMethodBody {
    private AbstractStringLiteral asmCode;

    public MethodAsmBody(AbstractStringLiteral asmCode) {
        Validate.notNull(asmCode);
        this.asmCode = asmCode;
    }

    // Getter
    public AbstractStringLiteral getAsmCode() {
        return asmCode;
    }

    // Setter
    public void setAsmCode(AbstractStringLiteral asmCode) {
        Validate.notNull(asmCode);
        this.asmCode = asmCode;
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("asm(");
        asmCode.decompile(s);
        s.println(");");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        asmCode.prettyPrint(s, prefix, true);
    }

    @Override
    public void iterChildren(TreeFunction f) {
        asmCode.iter(f);
    }

    @Override
    protected void verifyMethodBody(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass,
            Type returnType) throws ContextualError {
        // No verification needed for assembly method body
    }
}
