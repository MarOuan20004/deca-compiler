package fr.ensimag.ima.pseudocode;

/**
 * Operand representing a register indirection with offset and index, e.g. 42(R3, R4).
 *
 * @author Ensimag
 * @date 01/01/2026
 */
public class IndexedRegisterOffset extends DAddr {
    public int getOffset() {
        return offset;
    }
    public Register getBase() {
        return base;
    }
    public Register getIndex() {
        return index;
    }
    private final int offset;
    private final Register base;
    private final Register index;
    public IndexedRegisterOffset(int offset, Register base, Register index) {
        super();
        this.offset = offset;
        this.base = base;
        this.index = index;
    }
    @Override
    public String toString() {
        return offset + "(" + base + ", " + index + ")";
    }
}
