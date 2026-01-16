package fr.ensimag.deca.tree;

import org.apache.log4j.Logger;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.RTS;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.instructions.SUB;

/**
 *
 * @author gl10
 * @date 01/01/2026
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
            s.println();
        }
    }

    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass: start");
        for (AbstractDeclClass c : getList()) {
            c.verifyClass(compiler);//on parcourt la liste des classes puis on vérifie la passe 1
            
        }
        LOG.debug("verify listClass: end");
    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass: start");
        for (AbstractDeclClass c : getList()) {
            c.verifyClassMembers(compiler);//on parcourt la liste des classes puis on vérifie la passe 2
            
        }
        LOG.debug("verify listClass: end");
    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass: start");
        for (AbstractDeclClass c : getList()) {
            c.verifyClassBody(compiler);//on parcourt la liste des classes puis on vérifie la passe 3
            
        }
        LOG.debug("verify listClass: end");
    }

    public void codeGenListClass(DecacCompiler compiler) {
        genMethodObject(compiler);
        for (AbstractDeclClass c : getList()) {
            ((DeclClass) c).codeGenClass(compiler);
        }
    }

    
    public void genMethodObject(DecacCompiler compiler) {
            // Génération du code pour la méthode equals de la classe Object
            compiler.addLabel(new Label("Code.Object.equals"));
            // Comparer les deux références d'objet
            compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0)); // this
            compiler.addInstruction(new LOAD(new RegisterOffset(-3, Register.LB), Register.R1)); // other
            compiler.addInstruction(new SUB(Register.R0, Register.R1));
            // Si égales, retourner true (1), sinon false (0)
            Label labelTrue = new Label("equals_true");
            Label labelEnd = new Label("equals_end");
            compiler.addInstruction(new BEQ(labelTrue));
            // Faux
            compiler.addInstruction(new LOAD(new ImmediateInteger(0), Register.R0));
            compiler.addInstruction(new BRA(labelEnd));
            // Vrai
            compiler.addLabel(labelTrue);
            compiler.addInstruction(new LOAD(new ImmediateInteger(1), Register.R0));
            // Fin
            compiler.addLabel(labelEnd);
            compiler.addInstruction(new RTS());
        
    }


    public int codeGenMethTable(DecacCompiler compiler) {
        // pour object : 
        compiler.addLabel(new Label("method_table_object"));
        compiler.addInstruction(new LOAD (new NullOperand(), Register.R0));
        compiler.addInstruction(new STORE(Register.R0,new RegisterOffset(1, Register.GB)));
        Label equalsLabel = new Label("Code.Object.equals");
        compiler.addInstruction(new LOAD (new LabelOperand(equalsLabel), Register.R0));
        compiler.addInstruction(new STORE(Register.R0,new RegisterOffset(2, Register.GB)));

        int offset = 3;  // Object table occupe 2 positions
        for (AbstractDeclClass c : getList()) {
            DeclClass dc = (DeclClass) c;
            dc.codeGenMethTable(compiler, offset);
            // Après codeGenMethTable, getNumberOfMethods() contient le nombre de positions utilisées
            offset += dc.getClassName().getClassDefinition().getNumberOfMethods() + 1;
        }
        return offset;
    }


}
