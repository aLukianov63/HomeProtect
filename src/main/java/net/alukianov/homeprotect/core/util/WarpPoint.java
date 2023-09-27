package net.alukianov.homeprotect.core.util;

public final class WarpPoint extends BlockPoint {

    private State state;

    public WarpPoint(int x, int y, int z) {
        super(x, y, z);
        state = State.PRIVATE;
    }

    public State state() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        PUBLIC,
        PRIVATE
    }

}
