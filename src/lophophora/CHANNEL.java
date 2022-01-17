package lophophora;

public enum CHANNEL {
    ARCHON_LOC_1(0),
    ARCHON_LOC_2(1),
    ARCHON_LOC_3(2),
    ARCHON_LOC_4(3),
    fARCHON_STATUS1(4),
    fARCHON_STATUS2(5),
    fARCHON_STATUS3(6),
    fARCHON_STATUS4(7),
    ARCHON_ALIVE(8),
    UNIT_BUILT(9),
    MINERS_ALIVE(10),
    SOLDIERS_ALIVE(11),
    BUILDERS_ALIVE(12),
    MINING1(15),
    MINING2(16),
    MINING3(17),
    MINING4(18),
    MINING5(19),
    ORDERS(61),
    SEND_RANKS1(62),
    SEND_RANKS2(63), 
    ;

    private final int id;
    CHANNEL(int id) { this.id = id; }
    public int getValue() { return id; }
}
