package ro;

public class SymbolTableNode {
    private String key;
    private SymbolTableNode next;

    public SymbolTableNode(String key, SymbolTableNode next) {
        this.key = key;
        this.next = next;
    }

    public SymbolTableNode() {
        next = null;
        key = null;
    }

    public SymbolTableNode(SymbolTableNode n) {
        next = n.getNext();
        key = n.getKey();
    }

    public int containsKey(String key) {
        SymbolTableNode iterator = this;
        int i = 1;
        while (iterator != null) {
            if (iterator.key.equals(key)) return i;
            if (iterator.getNext() == null) iterator = null;
            else iterator = iterator.getNext();
            i += 1;
        }
        return -1;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public SymbolTableNode getNext() {
        return next;
    }

    public void setNext(SymbolTableNode next) {
        this.next = next;
    }

}
