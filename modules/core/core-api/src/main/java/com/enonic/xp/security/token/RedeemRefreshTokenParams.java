package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.IdProviderKey;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * What a redeem presents: the refresh token, the client presenting it and, optionally, the id
 * provider the grant's user must belong to. The conditions are checked before the token is acted
 * on, so a redeem that fails them changes nothing.
 */
@PublicApi
@NullMarked
public final class RedeemRefreshTokenParams
{
    private final String refreshToken;

    private final @Nullable IdProviderKey idProvider;

    private final String clientId;

    private RedeemRefreshTokenParams( final Builder builder )
    {
        this.refreshToken = requireNonNull( builder.refreshToken, "refreshToken is required" );
        this.idProvider = builder.idProvider;
        this.clientId = requireNonNull( builder.clientId, "clientId is required" );
        checkArgument( !this.clientId.isBlank(), "clientId must not be blank" );
    }

    /**
     * The refresh token presented.
     */
    public String getRefreshToken()
    {
        return refreshToken;
    }

    /**
     * The id provider the grant's user must belong to, or null for any.
     */
    public @Nullable IdProviderKey getIdProvider()
    {
        return idProvider;
    }

    /**
     * The client presenting the token. The grant must be for the same client id.
     */
    public String getClientId()
    {
        return clientId;
    }

    /**
     * A builder; the refresh token is required.
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Collects a redeem's token and conditions.
     */
    public static final class Builder
    {
        private @Nullable String refreshToken;

        private @Nullable IdProviderKey idProvider;

        private @Nullable String clientId;

        private Builder()
        {
        }

        /**
         * The refresh token presented. Required.
         */
        public Builder refreshToken( final String refreshToken )
        {
            this.refreshToken = refreshToken;
            return this;
        }

        /**
         * The id provider the grant's user must belong to; null (the default) for any.
         */
        public Builder idProvider( final @Nullable IdProviderKey idProvider )
        {
            this.idProvider = idProvider;
            return this;
        }

        /**
         * The client presenting the token. Required and not blank.
         */
        public Builder clientId( final String clientId )
        {
            this.clientId = clientId;
            return this;
        }

        /**
         * Builds the params.
         *
         * @throws NullPointerException when the refresh token is missing
         */
        public RedeemRefreshTokenParams build()
        {
            return new RedeemRefreshTokenParams( this );
        }
    }
}
