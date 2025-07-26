package com.hangout.core.profile_api.exceptions;

public class ConnectionFailedException extends MotherOfAllExceptions {
    public ConnectionFailedException(String clientMessage) {
        super(clientMessage);
    }
}
