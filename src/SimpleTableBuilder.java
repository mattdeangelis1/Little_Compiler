import java.util.ArrayList;
import java.util.Stack;

public class SimpleTableBuilder extends LittleBaseListener {

    public ArrayList<SymbolTable> symbolTableList;
    public Stack<SymbolTable> scopeStack;
    int currentBlockNumber = 1;

    @Override public void enterProgram(LittleParser.ProgramContext ctx) {

        SymbolTable global = new SymbolTable("GLOBAL");

        symbolTableList.add(global);

        scopeStack.push(global);

    }

    @Override public void exitProgram(LittleParser.ProgramContext ctx) {
        scopeStack.pop();
    }

    @Override
    public void enterWhile_stmt(LittleParser.While_stmtContext ctx){
        SymbolTable symbolTable = new SymbolTable("BLOCK " + currentBlockNumber);
        symbolTableList.add(symbolTable);
        scopeStack.push(symbolTable);
        currentBlockNumber++;
    }

    @Override public void exitWhile_stmt(LittleParser.While_stmtContext ctx){
        scopeStack.pop();
    }

    @Override public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
        SymbolTable symbolTable = new SymbolTable("BLOCK " + currentBlockNumber);
        symbolTableList.add(symbolTable);
        scopeStack.push(symbolTable);
    }

    @Override public void exitIf_stmt(LittleParser.If_stmtContext ctx){

        scopeStack.pop();

    }

    @Override
    public void enterVar_decl(LittleParser.Var_declContext ctx) {

        String type = ctx.var_type().getText();

        //id tail needs to account for declarations like INT a,b,c,d;
        //Will likely need to add a loop here
        String[] names = ctx.id_list().id_tail().getText().split(",");


        if (names.length == 1){
            scopeStack.peek().insert(new SymbolEntry<>(names[0], type, null));
        }

        //scopeStack.peek().insert(new SymbolEntry<>(name, type));

    }

    @Override public void enterString_decl(LittleParser.String_declContext ctx) {
        String name = ctx.id().getText();
        String type = "STRING";
        String value = ctx.str().getText();

        scopeStack.peek().insert(new SymbolEntry<String>(name, type, value));

    }

    public void prettyPrint(){

        for (SymbolTable symbolTable : symbolTableList) {

            System.out.println("Symbol table " + symbolTable.getScope());

            var current = symbolTable.getHead();

            while (current != null) {

                System.out.print("name " + current.name + " type " + current.type);

                if (current.value != null) {
                    System.out.println(" value \"" + current.value + "\"");
                }
                current = current.next;
            }
        }
    }
}
