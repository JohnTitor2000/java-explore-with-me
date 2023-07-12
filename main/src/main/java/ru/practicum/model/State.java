package ru.practicum.model;

public enum State {
    PENDING,
    PUBLISHED,
    CANCELED;

    public static State stateFromString(String value) {
        for (State state : State.values()) {
            if (state.name().equalsIgnoreCase(value)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid State value: " + value);
    }
}
