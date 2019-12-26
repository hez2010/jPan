package sysu.java;

public class Header {
    private static final Object lock = new Object();
    private static int counter;
    private int id;
    private int length;

    public Header() {
        synchronized (lock) {
            id = counter++;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
