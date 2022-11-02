package us.mcmagic.dreamwars.handlers;

public enum GameState {
    SERVER_STARTING, PRE_GAME, IN_GAME, POSTGAME;

    private static GameState currentState;

    public static void setState(GameState state) {
        GameState.currentState = state;
    }

    public static boolean isState(GameState state) {
        return GameState.currentState == state;
    }

    public static GameState getState() {
        return currentState;
    }
}