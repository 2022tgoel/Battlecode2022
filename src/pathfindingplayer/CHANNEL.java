package pathfindingplayer;

public enum CHANNEL {
    ARCHON_LOC_1(0),
    ARCHON_LOC_2(1),
    ARCHON_LOC_3(2),
    ARCHON_LOC_4(3),
    ORDERS(61),
    SEND_RANKS1(62),
    SEND_RANKS2(63);

    private final int id;
    CHANNEL(int id) { this.id = id; }
    public int getValue() { return id; }
}