package fr.ensimag.deca.tree;

import java.io.PrintStream;

import org.apache.commons.lang.Validate;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

import fr.ensimag.ima.pseudocode.InlinePortion;
import org.apache.commons.lang.StringEscapeUtils;

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
        Type type = asmCode.verifyExpr(compiler, localEnv, currentClass);
        if (!type.isString()) {
            throw new ContextualError("The asm body must be a string literal", asmCode.getLocation());
        }
    }

    @Override
    protected void codeGenMethodBody(DecacCompiler compiler, int nbSavedRegisters) {
        // Generate the assembly code directly
        String code = StringEscapeUtils.unescapeJava(asmCode.getValue());
        compiler.add(new InlinePortion(code));
    }

    @Override
    protected void codeGenMethodEnd(DecacCompiler compiler) {
        // Nothing to do for assembly body
    }
}
