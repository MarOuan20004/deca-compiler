package fr.ensimag.deca.context;

import fr.ensimag.deca.tools.SymbolTable;

public class TabType extends Type {
    private Type elementType;

    public TabType(SymbolTable.Symbol name, Type elementType) {
        super(name);
        this.elementType = elementType;
    }

    public Type getElementType() {
        return elementType;
    }

    @Override
    public boolean sameType(Type otherType) {
        if (!(otherType instanceof TabType)) {
            return false;
        }
        TabType other = (TabType) otherType;
        return elementType.sameType(other.elementType);
    }
}