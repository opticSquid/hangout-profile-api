package com.hangout.core.profile_api.exceptions;

public class AwsS3ClientException extends MotherOfAllExceptions {
    public AwsS3ClientException(String message, Exception e) {
        super(message, e);
    }
}
