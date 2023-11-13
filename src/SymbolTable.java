import java.util.HashSet;

public class SymbolTable {

    private String scope;

    private SymbolEntry<?> head;

    SymbolTable(String scope){
        this.scope = scope;
        this.head = null;
    }

    public String getScope(){
        return scope;
    }

    public SymbolEntry<?> getHead(){
        return head;
    }

    public <T> void insert(SymbolEntry<T> newEntry){
        newEntry.next = head;
        head = newEntry;
    }

    public SymbolEntry<?> lookup(String name) {
        SymbolEntry<?> current = head;
        while (current != null) {
            if (current.name.equals(name)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    public String hasDuplicates() {
        HashSet<String> set = new HashSet<>();
        SymbolEntry<?> current = head;

        while (current != null) {
            if (set.contains(current.name)) {
                return current.name;
            }
            set.add(current.name);
            current = current.next;
        }
        return null;
    }
}


