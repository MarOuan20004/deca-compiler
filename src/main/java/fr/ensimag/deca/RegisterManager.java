package fr.ensimag.deca;
import java.util.Stack;

import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;

public class RegisterManager {
    private int nbrRegistresMax = 16; // Par défaut R0-R15

    private Stack<Integer> libres = new Stack<>();

    public RegisterManager() {
        for (int i = 2; i < nbrRegistresMax; i++) {
            libres.push(i);
        }
    }

    public void setNbrRegistresMax(int n) {
        this.nbrRegistresMax = n;
    }

    public GPRegister allocateRegister() {
        if (!libres.isEmpty()) {
            return Register.getR(libres.pop());
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