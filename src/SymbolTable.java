public class SymbolTable {

    private SymbolEntry head;

    public void insert(String name,String type, String value){
        SymbolEntry newEntry = new SymbolEntry(name, type, value);
        newEntry.next = head;
        head = newEntry;
    }

    public SymbolEntry lookup(String name) {
        SymbolEntry current = head;
        while (current != null) {
            if (current.name.equals(name)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }


}
