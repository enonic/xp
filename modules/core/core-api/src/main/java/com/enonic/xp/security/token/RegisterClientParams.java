package com.enonic.xp.security.token;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;

/**
 * What a client says about itself when it registers on its own (RFC 7591): a name and links for
 * the consent page, the flows it will use, its redirect URIs and the most it will ask for.
 */
@NullMarked
public final class RegisterClientParams
{
    private final Client client;

    private RegisterClientParams( final Builder builder )
    {
        this.client = builder.client.id( "-" ).build();
    }

    /**
     * A builder for the parameters.
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * The client as declared, with a placeholder id.
     */
    public Client getClient()
    {
        return client;
    }

    /**
     * Collects the registration's values; {@link #build()} checks them.
     */
    public static final class Builder
    {
        private final Client.Builder client = Client.create();

        private Builder()
        {
        }

        /**
         * The id provider to register with. Required.
         */
        public Builder idProvider( final IdProviderKey idProvider )
        {
            client.idProvider( idProvider );
            return this;
        }

        /**
         * The name the consent page shows.
         */
        public Builder displayName( final @Nullable String displayName )
        {
            client.displayName( displayName );
            return this;
        }

        /**
         * The client's home page.
         */
        public Builder uri( final @Nullable String uri )
        {
            client.uri( uri );
            return this;
        }

        /**
         * The client's logo.
         */
        public Builder logoUri( final @Nullable String logoUri )
        {
            client.logoUri( logoUri );
            return this;
        }

        /**
         * The client's privacy policy.
         */
        public Builder policyUri( final @Nullable String policyUri )
        {
            client.policyUri( policyUri );
            return this;
        }

        /**
         * The client's terms of service.
         */
        public Builder tosUri( final @Nullable String tosUri )
        {
            client.tosUri( tosUri );
            return this;
        }

        /**
         * Adds grant types the client will use.
         */
        public Builder grantTypes( final Iterable<String> grantTypes )
        {
            client.grantTypes( grantTypes );
            return this;
        }

        /**
         * Adds redirect URIs.
         */
        public Builder redirectUris( final Iterable<String> redirectUris )
        {
            client.redirectUris( redirectUris );
            return this;
        }

        /**
         * Adds scopes to the most the client will ask for.
         */
        public Builder scope( final Iterable<String> scope )
        {
            client.scope( scope );
            return this;
        }

        /**
         * Adds audiences to the most the client will ask for.
         */
        public Builder resource( final Iterable<String> resource )
        {
            client.resource( resource );
            return this;
        }

        /**
         * @throws NullPointerException     when the id provider is missing
         * @throws IllegalArgumentException when a value is invalid, see {@link Client.Builder#build()}
         */
        public RegisterClientParams build()
        {
            return new RegisterClientParams( this );
        }
    }
}
