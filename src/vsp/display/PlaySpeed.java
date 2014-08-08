package vsp.display;

/**
 * An enumeration of play speeds.
 * @author adam
 */
public enum PlaySpeed {
    HALF_SPEED("0.5X"),
    ONE_X("1X"),
    TWO_X("2X"),
    FOUR_X("4X"),
    EIGHT_X("8X");

    /** A user-consumable display string for the speed. */
    private final String m_displayString;

    /**
     * Creates a new PlaySpeed with the supplied user-consumable string.
     * @param displayString
     */
    private PlaySpeed(String displayString) {
        m_displayString = displayString;
    }

    /**
     * Returns a user consumable display string for this speed option.
     * @return a user consumable display string for this speed option.
     */
    public String getDisplayString() {
        return m_displayString;
    }
}