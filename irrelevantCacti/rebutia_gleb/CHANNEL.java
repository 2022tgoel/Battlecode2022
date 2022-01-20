package rebutia_gleb;

public enum CHANNEL {
    ROUND_NUM(0),
    MINERS_ALIVE(1),
    SOLDIERS_ALIVE(2),
    BUILDERS_ALIVE(3),
    MINERS_ALIVE_ALT(4),
    SOLDIERS_ALIVE_ALT(5),
    BUILDERS_ALIVE_ALT(6),

    ARCHON_LOC_1(20),
    ARCHON_LOC_2(21),
    ARCHON_LOC_3(22),
    ARCHON_LOC_4(23),
    fARCHON_STATUS1(24),
    fARCHON_STATUS2(25),
    fARCHON_STATUS3(26),
    fARCHON_STATUS4(27),
    ARCHON_ALIVE(28),
    UNIT_BUILT(29),

    MINING1(30),
    TARGET(35),

    ORDERS(61),
    SEND_RANKS1(62),
    SEND_RANKS2(63), 
    ;

    public static final int NUM_TARGETS = 5;

    private final int id;
    CHANNEL(int id) { this.id = id; }
    public int getValue() { return id; }
}
