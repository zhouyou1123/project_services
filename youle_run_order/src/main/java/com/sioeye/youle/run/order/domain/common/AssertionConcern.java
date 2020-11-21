package com.sioeye.youle.run.order.domain.common;

public class AssertionConcern {
    protected AssertionConcern() {
        super();
    }

    protected void assertArgumentEquals(Object args1, Object args2, String message) {
        if (!args1.equals(args2)) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentFalse(boolean args, String message) {
        if (args) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentLength(String args, int max, String message) {
        int length = args.trim().length();
        if (length > max) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentLength(String args, int min, int max, String message) {
        int length = args.trim().length();
        if (length < min || length > max) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentNotEmpty(String args, String message) {
        if (args == null || args.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentNotEquals(Object args1, Object args2, String message) {
        if (args1.equals(args2)) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentNotNull(Object args, String message) {
        if (args == null) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentRange(double args, double min, double max, String message) {
        if (args < min || args > max) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentRange(float args, float min, float max, String message) {
        if (args < min || args > max) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentRange(int args, int min, int max, String message) {
        if (args < min || args > max) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentRange(long args, long min, long max, String message) {
        if (args < min || args > max) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertArgumentTrue(boolean args, String message) {
        if (!args) {
            throw new IllegalArgumentException(message);
        }
    }

    protected void assertStateFalse(boolean state, String message) {
        if (state) {
            throw new IllegalStateException(message);
        }
    }

    protected void assertStateTrue(boolean state, String message) {
        if (!state) {
            throw new IllegalStateException(message);
        }
    }
}
