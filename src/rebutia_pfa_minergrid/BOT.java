package rebutia_pfa_minergrid;

public enum BOT {
    NONE(0),
    MINER(1),
    SOLDIER(2),
    BUILDER(3),
    SAGE(4),
    WATCHTOWER(5),
    LABORATORY(6),
    ;

    private final int id;
    BOT(int id) { this.id = id; }
    public int getValue() { return id; }
}
