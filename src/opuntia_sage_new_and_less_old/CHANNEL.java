package opuntia_sage_new_and_less_old;

public enum CHANNEL {
    ROUND_NUM(0),

    MINERS_ALIVE(1),
    MINERS_ALIVE_ALT(2),
    SOLDIERS_ALIVE(3),
    SOLDIERS_ALIVE_ALT(4),
    BUILDERS_ALIVE(5),
    BUILDERS_ALIVE_ALT(6),
    TOWERS_ALIVE(7),
    TOWERS_ALIVE_ALT(8),
    LABS_ALIVE(9),
    LABS_ALIVE_ALT(10),
    SAGES_ALIVE(11),
    SAGES_ALIVE_ALT(12),

    USEFUL_MINERS(13),
    USEFUL_MINERS_ALT(14),

    REQUEST_LEAD(18),

    LEAD_ESTIMATE(19),
    ARCHON_LOC_1(20),
    fARCHON_STATUS1(24),
    ARCHON_NUMBER(28),
    UNIT_BUILT(29),

    MINING1(30),
    TARGET(35),
    ARCHON_MODE(40),
    ARCHON_MOVE(41), //for where to move
    //channels 30 - 33
    ARCHON_POSITION(44), //44-47
    ARCHON_MOVING(48), //for whether one is moving

    LAB_LOC(50),

    ORDERS(61),
    SEND_RANKS1(62),
    SEND_RANKS2(63), 
    ;
    
    public static final int NUM_TARGETS = 5;

    private final int id;
    CHANNEL(int id) { this.id = id; }
    public int getValue() { return id; }
}
