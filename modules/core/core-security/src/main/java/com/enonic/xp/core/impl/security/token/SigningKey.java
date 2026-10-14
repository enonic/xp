package com.enonic.xp.core.impl.security.token;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * A named HMAC secret. The key id ({@code kid}) goes into every token header so the verifier can
 * find the secret the token was signed with, which is what makes rotation possible.
 */
final class SigningKey
{
    /**
     * HMAC-SHA512 needs a key at least as long as its output (RFC 7518 section 3.2).
     */
    static final int MIN_SECRET_BYTES = 64;

    private final String kid;

    private final byte[] secret;

    SigningKey( final String kid, final byte[] secret )
    {
        this.kid = requireNonNull( kid );
        if ( kid.isBlank() )
        {
            throw new IllegalArgumentException( "Signing key id must not be blank" );
        }
        if ( secret.length < MIN_SECRET_BYTES )
        {
            throw new IllegalArgumentException(
                "Signing key [" + kid + "] is too short: " + secret.length + " bytes, at least " + MIN_SECRET_BYTES + " required" );
        }
        this.secret = secret.clone();
    }

    String kid()
    {
        return kid;
    }

    byte[] secret()
    {
        return secret.clone();
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof SigningKey that && kid.equals( that.kid ) && Arrays.equals( secret, that.secret );
    }

    @Override
    public int hashCode()
    {
        return 31 * kid.hashCode() + Arrays.hashCode( secret );
    }

    @Override
    public String toString()
    {
        return "SigningKey{" + kid + '}';
    }
}
