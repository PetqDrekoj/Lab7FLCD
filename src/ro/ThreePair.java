package ro;

public class ThreePair<T, T1, T2> {

    private T a;
    private T1 b;
    private T2 c;

    public ThreePair(T a, T1 b, T2 c){
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public T getA() {
        return a;
    }

    public void setA(T a) {
        this.a = a;
    }

    public T1 getB() {
        return b;
    }

    public void setB(T1 b) {
        this.b = b;
    }

    public T2 getC() {
        return c;
    }

    public void setC(T2 c) {
        this.c = c;
    }
}
