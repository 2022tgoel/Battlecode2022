package strawberryhedgehog;

public enum RANK { 
    DEFAULT(0),
    DEFENDER(1),
    FARMER(2);

    private final int id;
    RANK(int id) { this.id = id; }
    public int getValue() { return id; }
}
