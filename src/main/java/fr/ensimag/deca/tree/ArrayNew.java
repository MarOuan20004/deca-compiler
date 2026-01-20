package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.TabType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import java.io.PrintStream;

public class ArrayNew extends AbstractExpr {
    private AbstractIdentifier type;
    private AbstractExpr size;

    public ArrayNew(AbstractIdentifier type, AbstractExpr size) {
        this.type = type;
        this.size = size;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Type baseType = type.verifyType(compiler);
        Type sizeType = size.verifyExpr(compiler, localEnv, currentClass);
        
        if (!sizeType.isInt()) {
            throw new ContextualError("Array size must be int", getLocation());
        }
        
        Type arrayType = new TabType(compiler.createSymbol(type.getName().toString() + "[]"), baseType);
        setType(arrayType);
        return arrayType;
    }

    @Override
    public void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        // 1. Évaluer la taille
        size.codeGenExpr(compiler, r);
        
        // On sauvegarde la taille dans R1 car on va avoir besoin de r pour le résultat de NEW
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(r, fr.ensimag.ima.pseudocode.Register.R1));
        
        // 2. Allocation mémoire
        // Taille totale = taille demandée + 2 (pour stocker nLines et nCols)
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.ADD(new fr.ensimag.ima.pseudocode.ImmediateInteger(2), r));
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.NEW(r, r));
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BOV(new fr.ensimag.ima.pseudocode.Label("heap_overflow")));
        
        // 3. Stocker nLines (taille) et nCols (1 pour 1D)
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.STORE(fr.ensimag.ima.pseudocode.Register.R1, new fr.ensimag.ima.pseudocode.RegisterOffset(0, r)));
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(new fr.ensimag.ima.pseudocode.ImmediateInteger(1), fr.ensimag.ima.pseudocode.Register.R1));
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.STORE(fr.ensimag.ima.pseudocode.Register.R1, new fr.ensimag.ima.pseudocode.RegisterOffset(1, r)));
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        size.iter(f);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        type.decompile(s);
        s.print("[");
        size.decompile(s);
        s.print("]");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        size.prettyPrint(s, prefix, true);
    }
}
