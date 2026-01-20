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

public class Array2DNew extends AbstractExpr {
    private AbstractIdentifier type;
    private AbstractExpr rows;
    private AbstractExpr cols;

    public Array2DNew(AbstractIdentifier type, AbstractExpr rows, AbstractExpr cols) {
        this.type = type;
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        
        Type baseType = type.verifyType(compiler);
        Type rowsType = rows.verifyExpr(compiler, localEnv, currentClass);
        Type colsType = cols.verifyExpr(compiler, localEnv, currentClass);
        
        if (!rowsType.isInt()) {
            throw new ContextualError("Array rows must be int", getLocation());
        }
        if (!colsType.isInt()) {
            throw new ContextualError("Array cols must be int", getLocation());
        }
        
        Type innerArrayType = new TabType(compiler.createSymbol(type.getName().toString() + "[]"), baseType);
        Type array2dType = new TabType(compiler.createSymbol(type.getName().toString() + "[][]"), innerArrayType);
        setType(array2dType);
        return array2dType;
    }

    @Override
    public void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        GPRegister rRows = r;
        GPRegister rCols = compiler.getRegisterManager().allocateRegister();
        
        // 1. Évaluer rows et cols
        rows.codeGenExpr(compiler, rRows);
        cols.codeGenExpr(compiler, rCols);
        
        // On sauvegarde rows et cols pour les stocker après
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.PUSH(rRows));
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.PUSH(rCols));
        
        // 2. Calcul de la taille totale = rows * cols + 2
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.MUL(rCols, rRows));
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.ADD(new fr.ensimag.ima.pseudocode.ImmediateInteger(2), rRows));
        
        // 3. Allocation
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.NEW(rRows, rRows));
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BOV(new fr.ensimag.ima.pseudocode.Label("heap_overflow")));
        
        // 4. Stocker nLines et nCols
        // Récupérer rCols et rRows de la pile
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.POP(rCols)); // cols
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.POP(fr.ensimag.ima.pseudocode.Register.R1)); // rows (dans R1 pour ne pas polluer rRows qui contient l'adresse)
        
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.STORE(fr.ensimag.ima.pseudocode.Register.R1, new fr.ensimag.ima.pseudocode.RegisterOffset(0, rRows)));
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.STORE(rCols, new fr.ensimag.ima.pseudocode.RegisterOffset(1, rRows)));
        
        compiler.getRegisterManager().freeRegister(rCols);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        type.iter(f);
        rows.iter(f);
        cols.iter(f);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        type.decompile(s);
        s.print("[");
        rows.decompile(s);
        s.print("][");
        cols.decompile(s);
        s.print("]");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        rows.prettyPrint(s, prefix, false);
        cols.prettyPrint(s, prefix, true);
    }
}
