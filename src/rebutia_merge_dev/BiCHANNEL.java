package rebutia_merge_dev;

/**
 * For BiCHANNEL communications using one-round lagging updates.
 * Use for data that needs to be recomputed each round (ex. # of soldiers alive
 * this round).
 * BiCHANNEL alternate between two channels. Use Comms.getCounterChannel() to
 * get the update or read (lagging) channel.
 */
public enum BiCHANNEL {
    MINERS_ALIVE(CHANNEL.MINERS_ALIVE, CHANNEL.MINERS_ALIVE_ALT),
    SOLDIERS_ALIVE(CHANNEL.SOLDIERS_ALIVE, CHANNEL.SOLDIERS_ALIVE_ALT),
    BUILDERS_ALIVE(CHANNEL.BUILDERS_ALIVE, CHANNEL.BUILDERS_ALIVE_ALT),
    TOWERS_ALIVE(CHANNEL.TOWERS_ALIVE, CHANNEL.TOWERS_ALIVE_ALT),
    USEFUL_MINERS(CHANNEL.USEFUL_MINERS, CHANNEL.USEFUL_MINERS_ALT),
    ;

    final CHANNEL ch1;
    final CHANNEL ch2;

    BiCHANNEL(CHANNEL ch1, CHANNEL ch2) {
        this.ch1 = ch1;
        this.ch2 = ch2;
    }
}
