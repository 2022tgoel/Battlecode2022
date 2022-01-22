package schlumbergera;

public enum CHANNEL {
    // channels 0 - 3
    ARCHON_LOC_1(0),
    // channels 4 - 7
    fARCHON_STATUS1(4),
    ARCHON_NUMBER(8),
    UNIT_BUILT(9),
    MINERS_ALIVE(10),
    SOLDIERS_ALIVE(11),
    BUILDERS_ALIVE(12),
    // channels 15 - 19
    MINING1(15),
    // channels 20 - 24
    TARGET(20),
    ARCHON_MODE(26),
    //channels for archon moving specifically
    ARCHON_MOVE(27), //for where to move
    //channels 30 - 33
    ARCHON_POSITION(30),
    ARCHON_MOVING(34), //for whether one is moving
    //
    ORDERS(61),
    SEND_RANKS1(62),
    SEND_RANKS2(63), 
    ;

    public static final int NUM_TARGETS = 5;

    private final int id;
    CHANNEL(int id) { this.id = id; }
    public int getValue() { return id; }
}
