package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.annotation.PublicApi;

import static java.util.Objects.requireNonNull;

/**
 * What to verify: the token as presented by the client.
 */
@PublicApi
@NullMarked
public final class VerifyAccessTokenParams
{
    private final String token;

    private VerifyAccessTokenParams( final Builder builder )
    {
        this.token = requireNonNull( builder.token, "token is required" );
    }

    /**
     * The token in its serialized form, as sent by the client.
     */
    public String getToken()
    {
        return token;
    }

    /**
     * A builder; the token is required.
     */
    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private @Nullable String token;

        private Builder()
        {
        }

        /**
         * The token to verify. Required.
         */
        public Builder token( final String token )
        {
            this.token = token;
            return this;
        }

        /**
         * Builds the params.
         *
         * @throws NullPointerException when the token is missing
         */
        public VerifyAccessTokenParams build()
        {
            return new VerifyAccessTokenParams( this );
        }
    }
}
