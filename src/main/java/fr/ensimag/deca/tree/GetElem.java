package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.TabType;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import java.io.PrintStream;

public class GetElem extends AbstractExpr {
    private AbstractExpr matrix;
    private AbstractExpr row;
    private AbstractExpr col;

    public GetElem(AbstractExpr matrix, AbstractExpr row, AbstractExpr col) {
        this.matrix = matrix;
        this.row = row;
        this.col = col;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {

        Type matrixType = matrix.verifyExpr(compiler, localEnv, currentClass);
        Type rowType = row.verifyExpr(compiler, localEnv, currentClass);
        Type colType = col.verifyExpr(compiler, localEnv, currentClass);

        if (!rowType.isInt()) {
            throw new ContextualError(
                "getElem row index must be int",
                row.getLocation()
            );
        }

        if (!colType.isInt()) {
            throw new ContextualError(
                "getElem column index must be int",
                col.getLocation()
            );
        }

        Type elementType = matrixType;
        if (matrixType instanceof TabType) {
            elementType = ((TabType) matrixType).getElementType();
            if (elementType instanceof TabType) {
                elementType = ((TabType) elementType).getElementType();
            }
        }

        setType(elementType);
        return elementType;
    }

    @Override
    public int maxTemp() {
        return Math.max(matrix.maxTemp(), Math.max(row.maxTemp(), col.maxTemp())) + 1;
    }

    @Override
    public void codeGenExpr(DecacCompiler compiler, GPRegister r) {
        GPRegister rMatrix = r;
        GPRegister rRow = compiler.getRegisterManager().allocateRegister();
        GPRegister rCol = compiler.getRegisterManager().allocateRegister();
        GPRegister rTemp = compiler.getRegisterManager().allocateRegister();
        
        // 1. Évaluer matrix, row, col
        matrix.codeGenExpr(compiler, rMatrix);
        
        // Null check
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.CMP(new fr.ensimag.ima.pseudocode.NullOperand(), rMatrix));
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BEQ(new fr.ensimag.ima.pseudocode.Label("dereference_null")));
        
        row.codeGenExpr(compiler, rRow);
        col.codeGenExpr(compiler, rCol);
        
        // 2. Bounds check
        if (!compiler.getCompilerOptions().getNoCheck()) {
            // row check: 0 <= row < nLines (offset 0)
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(new fr.ensimag.ima.pseudocode.RegisterOffset(0, rMatrix), rTemp));
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.CMP(rTemp, rRow));
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BGE(new fr.ensimag.ima.pseudocode.Label("index_out_of_bounds")));
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.CMP(new fr.ensimag.ima.pseudocode.ImmediateInteger(0), rRow));
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BLT(new fr.ensimag.ima.pseudocode.Label("index_out_of_bounds")));
            
            // col check: 0 <= col < nCols (offset 1)
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(new fr.ensimag.ima.pseudocode.RegisterOffset(1, rMatrix), rTemp));
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.CMP(rTemp, rCol));
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BGE(new fr.ensimag.ima.pseudocode.Label("index_out_of_bounds")));
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.CMP(new fr.ensimag.ima.pseudocode.ImmediateInteger(0), rCol));
            compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.BLT(new fr.ensimag.ima.pseudocode.Label("index_out_of_bounds")));
        }

        // 3. Calculer l'adresse : base + 2 + row * nCols + col
        // Charger nCols (offset 1)
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(new fr.ensimag.ima.pseudocode.RegisterOffset(1, rMatrix), rTemp));
        
        // row * nCols
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.MUL(rTemp, rRow));
        // + col
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.ADD(rCol, rRow));
        // + 2
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.ADD(new fr.ensimag.ima.pseudocode.ImmediateInteger(2), rRow));
        
        // 4. Charger l'élément
        compiler.addInstruction(new fr.ensimag.ima.pseudocode.instructions.LOAD(new fr.ensimag.ima.pseudocode.IndexedRegisterOffset(0, rMatrix, rRow), r));
        
        compiler.getRegisterManager().freeRegister(rRow);
        compiler.getRegisterManager().freeRegister(rCol);
        compiler.getRegisterManager().freeRegister(rTemp);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        matrix.iter(f);
        row.iter(f);
        col.iter(f);
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print("getElem(");
        matrix.decompile(s);
        s.print(", ");
        row.decompile(s);
        s.print(", ");
        col.decompile(s);
        s.print(")");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        matrix.prettyPrint(s, prefix, false);
        row.prettyPrint(s, prefix, false);
        col.prettyPrint(s, prefix, true);
    }
}
