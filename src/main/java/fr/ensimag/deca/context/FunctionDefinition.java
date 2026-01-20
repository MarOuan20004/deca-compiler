package fr.ensimag.deca.context;

import fr.ensimag.deca.tree.Location;
import fr.ensimag.ima.pseudocode.Label;
import org.apache.commons.lang.Validate;

public class FunctionDefinition extends ExpDefinition {

    private final Signature signature;
    private Label label;
    
    public FunctionDefinition(Type type, Location location, Signature signature) {
        super(type, location);
        this.signature = signature;
    }

    public Signature getSignature() {
        return signature;
    }

    public Label getLabel() {
        Validate.isTrue(label != null,
                "setLabel() should have been called before");
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    @Override
    public String getNature() {
        return "function";
    }

    @Override
    public boolean isExpression() {
        return true;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public boolean isMethod() {
        return false;
    }
}
