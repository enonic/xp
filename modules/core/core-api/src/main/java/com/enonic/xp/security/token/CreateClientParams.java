package com.enonic.xp.security.token;

import java.time.Duration;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;

/**
 * A client to register with an id provider. The same values as the {@link Client} it creates.
 */
@NullMarked
public final class CreateClientParams
{
    private final Client client;

    private CreateClientParams( final Builder builder )
    {
        this.client = builder.client.build();
    }

    /**
     * A builder for the parameters.
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * The client as it will be stored.
     */
    public Client getClient()
    {
        return client;
    }

    /**
     * Collects the client's values; {@link #build()} checks them.
     */
    public static final class Builder
    {
        private final Client.Builder client = Client.create();

        private Builder()
        {
        }

        /**
         * The id provider to register the client with. Required.
         */
        public Builder idProvider( final IdProviderKey idProvider )
        {
            client.idProvider( idProvider );
            return this;
        }

        /**
         * The client id. Required.
         */
        public Builder id( final String id )
        {
            client.id( id );
            return this;
        }

        /**
         * The name the consent page shows; the id when null.
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
         * Adds grant types the client may use.
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
         * Adds scopes to the most the client may ask for.
         */
        public Builder scope( final Iterable<String> scope )
        {
            client.scope( scope );
            return this;
        }

        /**
         * Adds audiences to the most the client may ask for.
         */
        public Builder resource( final Iterable<String> resource )
        {
            client.resource( resource );
            return this;
        }

        /**
         * How long a grant to the client lives; null for the configured default.
         */
        public Builder grantTtl( final @Nullable Duration grantTtl )
        {
            client.grantTtl( grantTtl );
            return this;
        }

        /**
         * Whether the client may obtain tokens; true by default.
         */
        public Builder enabled( final boolean enabled )
        {
            client.enabled( enabled );
            return this;
        }

        /**
         * Whether XP seeds the client; false by default.
         */
        /**
         * @throws NullPointerException     when the id provider or the id is missing
         * @throws IllegalArgumentException when a value is invalid, see {@link Client.Builder#build()}
         */
        public CreateClientParams build()
        {
            return new CreateClientParams( this );
        }
    }
}
