package ro;

public class MyPair<T, T1> {
    private T first;
    private T1 second;

    public MyPair(T first, T1 second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public T1 getSecond() {
        return second;
    }

    public void setSecond(T1 second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "{" +
                "" + first +
                ", " + second +
                '}';
    }
}
