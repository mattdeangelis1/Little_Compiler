import java.util.ArrayList;
import java.util.Stack;

public class SimpleTableBuilder extends LittleBaseListener {

    public ArrayList<SymbolTable> symbolTableList = new ArrayList<>();
    public Stack<SymbolTable> scopeStack = new Stack<>();
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


    @Override public void enterElse_part(LittleParser.Else_partContext ctx) {

        SymbolTable symbolTable = new SymbolTable("BLOCK " + currentBlockNumber);
        symbolTableList.add(symbolTable);
        scopeStack.push(symbolTable);
        currentBlockNumber++;

    }

    @Override public void exitElse_part(LittleParser.Else_partContext ctx) {

        scopeStack.pop();

    }

    @Override public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
        SymbolTable symbolTable = new SymbolTable("BLOCK " + currentBlockNumber);
        symbolTableList.add(symbolTable);
        scopeStack.push(symbolTable);
        currentBlockNumber++;
    }

    @Override public void exitIf_stmt(LittleParser.If_stmtContext ctx){
        scopeStack.pop();
    }

    @Override public void enterFunc_decl(LittleParser.Func_declContext ctx) {
        SymbolTable symbolTable = new SymbolTable(ctx.id().getText());


        if (ctx.param_decl_list() != null && ctx.param_decl_list().param_decl() != null) {
            if (ctx.param_decl_list().param_decl().var_type() != null && ctx.param_decl_list().param_decl().id() != null) {

                symbolTable.insert(new SymbolEntry<>(ctx.param_decl_list().param_decl().id().getText(), ctx.param_decl_list().param_decl().var_type().getText(), null));

            }
        }

        symbolTableList.add(symbolTable);
        scopeStack.push(symbolTable);
    }

    @Override public void exitFunc_decl(LittleParser.Func_declContext ctx){
        scopeStack.pop();
    }


    @Override
    public void enterVar_decl(LittleParser.Var_declContext ctx) {

        String type = ctx.var_type().getText();

        //id tail needs to account for declarations like INT a,b,c,d

       // System.out.println(ctx.id_list().getText());

        String[] names = ctx.id_list().getText().split(",");

        for (String name : names) {

            scopeStack.peek().insert(new SymbolEntry<>(name, type, null));

        }

    }

    @Override public void enterString_decl(LittleParser.String_declContext ctx) {

        String name = ctx.id().getText();
        String type = "STRING";
        String value = ctx.str().getText() != null  ? ctx.str().getText() : null;

        scopeStack.peek().insert(new SymbolEntry<String>(name, type, value));

    }

    public void prettyPrint() {

        for (SymbolTable symbolTable : symbolTableList) {

            System.out.println("Symbol table " + symbolTable.getScope());

            SymbolEntry<?> current = symbolTable.getHead();

            Stack<SymbolEntry<?>> stack = new Stack<>();

            while (current != null) {

                stack.push(current);

                current = current.next;
            }

            while(!stack.isEmpty()){

                current = stack.pop();

                System.out.print("name " + current.name + " type " + current.type);

                if (current.value != null) {
                    System.out.println(" value " + current.value);
                }else{
                    System.out.println();
                }

            }

            System.out.println();

        }
    }
}
