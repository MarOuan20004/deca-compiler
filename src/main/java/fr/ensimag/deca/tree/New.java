package fr.ensimag.deca.tree;

import java.io.PrintStream;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.BOV;
import fr.ensimag.ima.pseudocode.instructions.BSR;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.NEW;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.STORE;


/**
 *
 * @author gl10
 * @date 13/01/2026
 */

public class New extends AbstractExpr{
    private AbstractIdentifier classNom;

    public New(AbstractIdentifier classNom) {
        this.classNom = classNom;
    }
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
                Type typeClass = classNom.verifyType(compiler) ;
        if(!typeClass.isClass()){
            throw new ContextualError("Only a class can use new ", classNom.getLocation());
        }
        this.setType(typeClass);
        return typeClass;
        }


    @Override
    public void decompile(IndentPrintStream s) {
        s.print("new ");
        classNom.decompile(s);
        s.print("()");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        classNom.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        classNom.prettyPrint(s, prefix, true);
    }

    @Override
public void codeGenExpr(DecacCompiler compiler, GPRegister r) {

    ClassDefinition classDef = classNom.getClassDefinition();
    int size = classDef.getNumberOfFields() + 1;
    compiler.addInstruction(new NEW(new ImmediateInteger(size), r));

    compiler.addInstruction(new BOV(new Label("heap_overflow")));
    compiler.addInstruction(new LEA(classDef.getAdrTable(), Register.R1));
    compiler.addInstruction(new STORE(Register.R1, new RegisterOffset(0, r)));
    // On sauvegarde l'objet avant l'appel
    compiler.addInstruction(new PUSH(r));
    // On appelle la routine d'initialisation
    compiler.addInstruction(new BSR(new Label("init." + classNom.getName().getName())));
    // On restaure l'objet dans r
    compiler.addInstruction(new POP(r));
}
}
    

