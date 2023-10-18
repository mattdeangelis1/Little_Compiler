public class SymbolEntry {

    String name;
    String type;
    String value;

    SymbolEntry next;

    public SymbolEntry(String name, String type, String value){
        this.name = name;
        this.type = type;
        this.value = value;
        this.next = null;
    }

}
