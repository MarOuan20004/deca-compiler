package fr.ensimag.deca.context;

import fr.ensimag.deca.DecacCompiler;


import java.util.HashMap;
import java.util.Map;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;


// A FAIRE: étendre cette classe pour traiter la partie "avec objet" de Déca
/**
 * Environment containing types. Initially contains predefined identifiers, more
 * classes can be added with declareClass().
 *
 * @author gl10
 * @date 01/01/2026
 */
public class EnvironmentType {
    public EnvironmentType(DecacCompiler compiler) {
        
        envTypes = new HashMap<Symbol, TypeDefinition>();
        
        Symbol intSymb = compiler.createSymbol("int");
        INT = new IntType(intSymb);
        envTypes.put(intSymb, new TypeDefinition(INT, Location.BUILTIN));

        Symbol floatSymb = compiler.createSymbol("float");
        FLOAT = new FloatType(floatSymb);
        envTypes.put(floatSymb, new TypeDefinition(FLOAT, Location.BUILTIN));

        Symbol voidSymb = compiler.createSymbol("void");
        VOID = new VoidType(voidSymb);
        envTypes.put(voidSymb, new TypeDefinition(VOID, Location.BUILTIN));

        Symbol booleanSymb = compiler.createSymbol("boolean");
        BOOLEAN = new BooleanType(booleanSymb);
        envTypes.put(booleanSymb, new TypeDefinition(BOOLEAN, Location.BUILTIN));

        Symbol stringSymb = compiler.createSymbol("string");
        STRING = new StringType(stringSymb);
        // Creation of the Object class
        Symbol objectSymb = compiler.createSymbol("Object");
        OBJECT = new ClassType(objectSymb, Location.BUILTIN, null);
        ClassDefinition objectDef = OBJECT.getDefinition();
        objectDef.setAdrTable(new RegisterOffset(1, Register.GB));
        // add equals method to Object class
        Symbol equalsSymb = compiler.createSymbol("equals");
        Signature equalsSig = new Signature();
        equalsSig.add(OBJECT);
        MethodDefinition equalsDef = new MethodDefinition(
            BOOLEAN,Location.BUILTIN,equalsSig,1);
        try {
            objectDef.getMembers().declare(equalsSymb, equalsDef);
        } catch (EnvironmentExp.DoubleDefException e) {
            throw new InternalError("double definition dans la classe Object");
        }
        // Set numberOfMethods for Object class to 1 (has equals method)
        objectDef.setNumberOfMethods(1);
        envTypes.put(objectSymb, objectDef);

        
    }
    public void envTypesAdd(Symbol symbol , TypeDefinition typeDefinition , Location location) throws  ContextualError{
        TypeDefinition typeDef = this.defOfType(symbol);
        if(typeDef != null){
            throw new ContextualError("la symbol à ajouté existe deja dans EnvType", location);
        }
        else{
        envTypes.put(symbol, typeDefinition);
        }
    }

    private final Map<Symbol, TypeDefinition> envTypes;

    public TypeDefinition defOfType(Symbol s) {
        return envTypes.get(s);
    }

    public final VoidType    VOID;
    public final IntType     INT;
    public final FloatType   FLOAT;
    public final StringType  STRING;
    public final BooleanType BOOLEAN;
    public final ClassType   OBJECT;
}
