package rebutia_merge_dev;

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
    ARCHON_LOC_1(20),
    fARCHON_STATUS1(24),
    ARCHON_NUMBER(28),
    UNIT_BUILT(29),

    MINING1(30),
    // update: use targets to indicate how many more soldiers are required
    TARGET1(35),
    TARGET5(39),
    ARCHON_MODE(40),
    // // indicates a location and a # of enemy units
    // // cleared every three rounds in case the calling soldier dies
    // // therefore, every soldier must call on a N%3=0 round, calls
    // // are tallied on a N%3=1 round, and calls are decided on a N%3=2 round.
    // RUSH_INDICATOR1(41),
    // RUSH_INDICATOR5(45),

    ORDERS(61),
    SEND_RANKS1(62),
    SEND_RANKS2(63), 
    ;
    
    public static final int NUM_TARGETS = 5;

    private final int id;
    CHANNEL(int id) { this.id = id; }
    public int getValue() { return id; }
}
