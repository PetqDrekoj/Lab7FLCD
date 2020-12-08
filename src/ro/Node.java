package ro;

public class Node {
    private int crt;
    private String node;
    private int father;
    private int right_sibling;

    public Node(int crt, String node, int father, int sibling) {
        this.crt = crt;
        this.node = node;
        this.father = father;
        this.right_sibling = sibling;
    }

    @Override
    public String toString() {
        return "Node[" + crt + ", " + node + ", " + father + ", " + right_sibling + "]";
    }

    public int getCrt() {
        return crt;
    }

    public void setCrt(int crt) {
        this.crt = crt;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public int getFather() {
        return father;
    }

    public void setFather(int father) {
        this.father = father;
    }

    public int getRight_sibling() {
        return right_sibling;
    }

    public void setRight_sibling(int right_sibling) {
        this.right_sibling = right_sibling;
    }
}
