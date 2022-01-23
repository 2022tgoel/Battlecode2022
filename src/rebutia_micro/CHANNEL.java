package rebutia_micro;

public enum CHANNEL {
    ROUND_NUM(0),
    MINERS_ALIVE(1),
    SOLDIERS_ALIVE(2),
    BUILDERS_ALIVE(3),
    TOWERS_ALIVE(4),
    MINERS_ALIVE_ALT(5),
    SOLDIERS_ALIVE_ALT(6),
    BUILDERS_ALIVE_ALT(7),
    TOWERS_ALIVE_ALT(8),
    USEFUL_MINERS(9),
    USEFUL_MINERS_ALT(10),

    LEAD_ESTIMATE(19),
    ENEMY_ARCHON_LOCATION1(20),
    FRIENDLY_ARCHON_STATUS1(24),
    ARCHON_NUMBER(28),
    UNIT_BUILT(29),
    FRIENDLY_ARCHON_LOCATION1(30),

    MINING1(30),
    TARGET(35),
    ARCHON_MODE(40),

    ORDERS(61),
    SEND_RANKS1(62),
    SEND_RANKS2(63), 
    ;
    
    public static final int NUM_TARGETS = 5;

    private final int id;
    CHANNEL(int id) { this.id = id; }
    public int getValue() { return id; }
}
