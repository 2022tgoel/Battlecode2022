package astrophytum;

public enum RANK { 
    DEFAULT(0),
    DEFENDER(2);

    private final int id;
    RANK(int id) { this.id = id; }
    public int getValue() { return id; }
}
