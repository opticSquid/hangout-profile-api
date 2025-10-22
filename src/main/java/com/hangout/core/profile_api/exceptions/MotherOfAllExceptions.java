package com.hangout.core.profile_api.exceptions;

public class MotherOfAllExceptions extends RuntimeException {
    public MotherOfAllExceptions(String clientMessage) {
        super(clientMessage);
    }

    public MotherOfAllExceptions(String clientMessage, Exception ex) {
        super(clientMessage, ex);
    }
}
