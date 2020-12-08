package ro;

import java.util.ArrayList;
import java.util.List;

public class SymbolTable {

    private List<SymbolTableNode> hashTable;

    public SymbolTable() {
        hashTable = new ArrayList<>();
        for (int i = 0; i < 73; i++) hashTable.add(null);
    }

    public List<SymbolTableNode> getHashTable() {
        return hashTable;
    }

    private int hashFunction(String key) {
        int multiplier = 3;
        int total_sum = 0;
        for (int i = 0; i < key.length(); i++) {
            total_sum += ((int) key.charAt(i)) * multiplier;
            multiplier += 2;
        }
        return total_sum % this.getHashTable().size();
    }

    public String put(String key) {
        int hashed_key = hashFunction(key);
        if (hashTable.get(hashed_key) == null) {
            SymbolTableNode element = new SymbolTableNode(key,  null);
            hashTable.set(hashed_key, element);
            return null;
        } else if (hashTable.get(hashed_key).containsKey(key) != -1) {
            return key;
        } else {
            SymbolTableNode element = new SymbolTableNode(key, null);
            SymbolTableNode iterator = hashTable.get(hashed_key);
            while (iterator.getNext() != null) {
                iterator = iterator.getNext();
            }
            iterator.setNext(element);
            return null;
        }
    }

    public String get(String key) {
        int hashed_key = hashFunction(key);
        if (hashTable.get(hashed_key) == null || hashTable.get(hashed_key).containsKey(key)== -1) return null;
        else {
            return key;
        }
    }

    public MyPair<Integer,Integer> getPositionOfKey(String key){
        return new MyPair<>(hashFunction(key),hashTable.get(hashFunction(key)).containsKey(key));
    }

    @Override
    public String toString() {
        return "SymbolTable{" +
                "hashTable=" + hashTable +
                '}';
    }
}
