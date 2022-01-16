package kiwi;

public enum CHANNEL {
    // channels 0 - 3
    ENEMY_ARCHON_LOCATION(0),
    // channels 4 - 7
    FRIENDLY_ARCHON_STATUS(4),
    ARCHON_ALIVE(8),
    UNIT_BUILT(9),
    MINERS_ALIVE(10),
    SOLDIERS_ALIVE(11),
    BUILDERS_ALIVE(12),
    // channels 15 - 19
    MINING1(15),
    // channels 20 - 24
    TARGET(20),
    // channels 25 - 28
    FRIENDLY_ARCHON_LOCATION(25),
    ORDERS(61),
    SEND_RANKS1(62),
    SEND_RANKS2(63),
    ;

    public static final int NUM_TARGETS = 5;

    private final int id;

    CHANNEL(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }
}
