package com.hangout.core.profile_api.template;

import java.math.BigInteger;

public record Session(BigInteger userId, String role, Boolean trustedDevice) {
}