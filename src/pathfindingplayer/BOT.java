package pathfindingplayer;

public enum BOT {
    NONE(0),
    MINER(1),
    SOLDIER(2),
    ;

    private final int id;
    BOT(int id) { this.id = id; }
    public int getValue() { return id; }
}
