public class SymbolEntry<T> {

    String name;
    String type;
    T value = null;

    SymbolEntry<?> next;

    public SymbolEntry(String name, String type, T value){
        this.name = name;
        this.type = type;
        this.value = value;
        this.next = null;
    }

}
