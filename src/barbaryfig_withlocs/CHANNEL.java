package barbaryfig_withlocs;

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

    /*
     * Approximate locations are enumerated by dividing the map into a 4x4 grid.
     * The grid is numbered from 0 to 15.
     * Row = Num / 4; Col = Num % 4.
     * There are 4 grid squares per channel. So, grid squares occupy 16/4 = 4
     * channels.
     */
    /**
     * Acts as a base. Approximate location channels occupy up to channel 23.
     */
    GRID_BASE(20),

    /*
     * Denotes which grid squares are the most active, so soldiers don't have to.
     * There are 16 grid squares, so locating one requires 4 bits. Grid squares are
     * verified before soldiers travel to them, so empty grid squares don't matter.
     */
    TOP4_ACTIVE_GRID_SQUARES(24),

    ORDERS(61),
    SEND_RANKS1(62),
    SEND_RANKS2(63),
    ;

    public static final int GRID_SQUARES_PER_CHANNEL = 4;

    public static final int GRID_SIZE = 4;

    private final int id;

    CHANNEL(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }
}
