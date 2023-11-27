import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Pattern;

public class SimpleTableBuilder extends LittleBaseListener {


    public ArrayList<SymbolTable> symbolTableList = new ArrayList<>();
    public Stack<SymbolTable> scopeStack = new Stack<>();
    int currentBlockNumber = 1;
    boolean errorDetected = false;
    ArrayList<String> errorVarNames = new ArrayList<>();
    int register = 1;

    public void duplicateChecker(String id){

        if (scopeStack.peek().lookup(id) != null){
            errorVarNames.add(id);
            errorDetected = true;
        }

    }


    @Override public void enterProgram(LittleParser.ProgramContext ctx) {
        SymbolTable global = new SymbolTable("GLOBAL");
        symbolTableList.add(global);
        scopeStack.push(global);
    }

    @Override public void exitProgram(LittleParser.ProgramContext ctx) {
        System.out.println(";RET\n;tiny code");
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

        if (!ctx.getText().isEmpty()){
            SymbolTable symbolTable = new SymbolTable("BLOCK " + currentBlockNumber);
            symbolTableList.add(symbolTable);
            scopeStack.push(symbolTable);
            currentBlockNumber++;
        }

    }

    @Override public void exitElse_part(LittleParser.Else_partContext ctx) {

        if (!ctx.getText().isEmpty()){
            scopeStack.pop();
        }

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

                LittleParser.Param_decl_tailContext current = ctx.param_decl_list().param_decl_tail();

                while (current.param_decl_tail() != null) {

                    symbolTable.insert(new SymbolEntry<>(current.param_decl().id().getText(), current.param_decl().var_type().getText(), null));
                    current = current.param_decl_tail();
                }

            }
        }

        System.out.println(";LABEL " + ctx.id().getText());
        System.out.println(";LINK");

        symbolTableList.add(symbolTable);
        scopeStack.push(symbolTable);
    }

    @Override public void exitFunc_decl(LittleParser.Func_declContext ctx){


        if (scopeStack.peek().hasDuplicates() != null){
            errorVarNames.add(scopeStack.peek().hasDuplicates());
            errorDetected = true;
        }



        scopeStack.pop();
    }

    @Override
    public void enterVar_decl(LittleParser.Var_declContext ctx) {

        String type = ctx.var_type().getText();

        String[] names = ctx.id_list().getText().split(",");

        for (String name : names) {

            duplicateChecker(name);
            scopeStack.peek().insert(new SymbolEntry<>(name, type, null));

        }
    }

    @Override public void enterString_decl(LittleParser.String_declContext ctx) {

        String name = ctx.id().getText();
        String type = "STRING";
        String value = ctx.str().getText() != null  ? ctx.str().getText() : null;

        duplicateChecker(name);
        scopeStack.peek().insert(new SymbolEntry<>(name, type, value));

    }


    @Override public void enterWrite_stmt(LittleParser.Write_stmtContext ctx) {

        String[] ids = ctx.id_list().getText().split(",");

        for (String id : ids){

            for (SymbolTable table : symbolTableList) {
                if (table.lookup(id) != null) {
                    System.out.println(";WRITE" + table.lookup(id).type.charAt(0) + " " + id);
                    break;
                }
            }

        }

    }


    @Override public void enterRead_stmt(LittleParser.Read_stmtContext ctx) {
        String[] ids = ctx.id_list().getText().split(",");

        for (String id : ids){

            for (SymbolTable table : symbolTableList) {
                if (table.lookup(id) != null) {
                    System.out.println(";READ" + table.lookup(id).type.charAt(0) + " " + id);
                    break;
                }
            }

        }

    }

    @Override public void enterAssign_expr(LittleParser.Assign_exprContext ctx) {

        String type = "";

        for (SymbolTable table : symbolTableList){
            if (table.lookup(ctx.getText().split(":=")[0]) != null){

                type = table.lookup(ctx.getText().split(":=")[0]).type;
                break;

            }
        }

        if (ctx.expr().getText().contains("+") || ctx.expr().getText().contains("-") || ctx.expr().getText().contains("/") || ctx.expr().getText().contains("*")){

            String operation = Character.toString(ctx.expr().getText().charAt(1));
            String first = ctx.getText().split(Pattern.quote(operation))[0].split(":=")[1];
            String second = ctx.getText().split(Pattern.quote(operation))[1];

            if (operation.equals("*")){
                System.out.println(";MULT" + type.charAt(0) + " " + first + " " + second + " $T" + register);
            }else if(operation.equals("/")){
                System.out.println(";DIV" + type.charAt(0) + " " + first + " " + second + " $T" + register);
            }else if(operation.equals("-")){
                System.out.println(";SUB" + type.charAt(0) + " " + first + " " + second + " $T" + register);
            }else if(operation.equals("+")){
                System.out.println(";ADD" + type.charAt(0) + " " + first + " " + second + " $T" + register);
            }
            System.out.println(";STORE" + type.charAt(0) + " $T" + register + " " + ctx.getText().split(":=")[0]);
            register++;

        }else{
            System.out.println(";STORE"  + type.charAt(0) + " " + ctx.getText().split(":=")[1] + " $T" + register);
            System.out.println(";STORE"  + type.charAt(0) + " $T" + register + " " + ctx.getText().split(":=")[0]);
            register++;
        }

    }


    public void prettyPrint() {

        if (errorDetected){
            System.out.println("DECLARATION ERROR " + errorVarNames.get(0));
            return;
        }

        for (int i = 0;i < symbolTableList.size();i++) {

            System.out.println("Symbol table " + symbolTableList.get(i).getScope());

            SymbolEntry<?> current = symbolTableList.get(i).getHead();

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

            if (i!=symbolTableList.size()-1){
                System.out.println();
            }


        }
    }

    



}
