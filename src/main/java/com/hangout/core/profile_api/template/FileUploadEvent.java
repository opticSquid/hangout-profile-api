package com.hangout.core.profile_api.template;

import java.math.BigInteger;

public record FileUploadEvent(String filename, BigInteger userId) {

}
