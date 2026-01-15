package fr.ensimag.deca;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;

public class RegisterManager {
    private int nbrRegistresMax = 16; // Par défaut R0-R15

    private Stack<Integer> libres = new Stack<>();
    private Set<GPRegister> usedInCurrentMethod = new HashSet<>();

    public RegisterManager() {
        for (int i = nbrRegistresMax - 1; i >= 2; i--) {
            libres.push(i);
        }
    }

    /**
     * Prepares the manager for a new method code generation scope.
     */
    public void beginMethodScope() {
        usedInCurrentMethod.clear();
    }

    /**
     * Gets the set of registers that have been used in the current method scope.
     * @return An unmodifiable set of used GPRegister.
     */
    public Set<GPRegister> getUsedInCurrentMethod() {
        return Collections.unmodifiableSet(usedInCurrentMethod);
    }

    public void setNbrRegistresMax(int n) {
        this.nbrRegistresMax = n;
    }

    public GPRegister allocateRegister() {
        if (!libres.isEmpty()) {
            GPRegister reg = Register.getR(libres.pop());
            usedInCurrentMethod.add(reg);
            return reg;
        }
        return null; // aucun registre libre
    }

    public void freeRegister(GPRegister r) {
        int num = r.getNumber();
        if (num >= 2 && num < nbrRegistresMax) {
            libres.push(num);
        }
    }

}