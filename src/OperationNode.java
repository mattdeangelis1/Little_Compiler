public class OperationNode extends ASTNode{

    private final char operator;
    private ASTNode left;
    private ASTNode right;

    OperationNode(char operator){
        this.operator = operator;
    }

    @Override
    public String toString() {
        return String.valueOf(operator);
    }
}
