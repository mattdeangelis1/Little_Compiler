public class NumberNode extends ASTNode{

    private final Number value;

    public NumberNode(Number value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
