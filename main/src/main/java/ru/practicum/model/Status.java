package ru.practicum.model;

public enum Status {
    PENDING,
    REJECTED,
    APPROVE;

    public static Status statusFromString(String value) {
        for (Status status : Status.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid State value: " + value);
    }
}
