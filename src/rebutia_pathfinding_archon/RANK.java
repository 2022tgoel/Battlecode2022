package rebutia_pathfinding_archon;

public enum RANK { 
    DEFAULT(0),
    DEFENDER(1),
    MARTYR(2);

    private final int id;
    RANK(int id) { this.id = id; }
    public int getValue() { return id; }
}
