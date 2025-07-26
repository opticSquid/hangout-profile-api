package com.hangout.core.profile_api.exceptions;

public class DuplicateProfileException extends MotherOfAllExceptions {
    public DuplicateProfileException(String clientMessage) {
        super(clientMessage);
    }
}
