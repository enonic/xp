package com.enonic.xp.security.token;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Objects;
import java.util.Set;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.security.IdProviderKey;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * A client registered with an id provider: its name and links for the consent page, the grant
 * types it may use, its redirect URIs, the most scopes and audiences it may ask for, how long its
 * grants live, and whether it may obtain tokens at all.
 */
@NullMarked
public final class Client
{
    private final IdProviderKey idProvider;

    private final String id;

    private final String displayName;

    private final @Nullable String uri;

    private final @Nullable String logoUri;

    private final @Nullable String policyUri;

    private final @Nullable String tosUri;

    private final ImmutableSet<String> grantTypes;

    private final ImmutableSet<String> redirectUris;

    private final ImmutableSet<String> scope;

    private final ImmutableSet<String> resource;

    private final @Nullable Duration grantTtl;

    private final boolean enabled;

    private Client( final Builder builder )
    {
        this.idProvider = requireNonNull( builder.idProvider, "idProvider is required" );
        this.id = requireNonNull( builder.id, "id is required" );
        checkArgument( !this.id.isBlank(), "id must not be blank" );
        this.displayName = builder.displayName == null ? this.id : builder.displayName;
        this.uri = checkWebUri( builder.uri, "uri" );
        this.logoUri = checkWebUri( builder.logoUri, "logoUri" );
        this.policyUri = checkWebUri( builder.policyUri, "policyUri" );
        this.tosUri = checkWebUri( builder.tosUri, "tosUri" );
        this.grantTypes = builder.grantTypes.build();
        for ( final String grantType : this.grantTypes )
        {
            checkArgument( GrantTypes.isKnown( grantType ), "unknown grant type: %s", grantType );
        }
        this.redirectUris = builder.redirectUris.build();
        for ( final String redirectUri : this.redirectUris )
        {
            checkUri( redirectUri, "redirectUri" );
        }
        this.scope = builder.scope.build();
        this.resource = builder.resource.build();
        this.grantTtl = builder.grantTtl;
        checkArgument( grantTtl == null || ( !grantTtl.isNegative() && !grantTtl.isZero() ), "grantTtl must be positive: %s", grantTtl );
        this.enabled = builder.enabled;
    }

    /**
     * A builder for a client.
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * The consent page shows the client's links to the user, so they must lead to a web page or an image.
     */
    private static @Nullable String checkWebUri( final @Nullable String value, final String what )
    {
        if ( value == null )
        {
            return null;
        }
        final String scheme = checkUri( value, what ).getScheme();
        checkArgument( "http".equalsIgnoreCase( scheme ) || "https".equalsIgnoreCase( scheme ), "%s must be an http or https URL: %s", what,
                       value );
        return value;
    }

    private static URI checkUri( final String value, final String what )
    {
        try
        {
            final URI uri = new URI( value );
            checkArgument( uri.isAbsolute() && uri.getFragment() == null, "%s must be an absolute URI without a fragment: %s", what, value );
            return uri;
        }
        catch ( URISyntaxException e )
        {
            throw new IllegalArgumentException( what + " is not a URI: " + value, e );
        }
    }

    /**
     * The id provider the client belongs to.
     */
    public IdProviderKey getIdProvider()
    {
        return idProvider;
    }

    /**
     * The client id, as the client sends it in {@code client_id}. Unique within the id provider.
     */
    public String getId()
    {
        return id;
    }

    /**
     * The name the consent page shows. The id when none was given.
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * The client's home page, or null.
     */
    public @Nullable String getUri()
    {
        return uri;
    }

    /**
     * The client's logo, or null.
     */
    public @Nullable String getLogoUri()
    {
        return logoUri;
    }

    /**
     * The client's privacy policy, or null.
     */
    public @Nullable String getPolicyUri()
    {
        return policyUri;
    }

    /**
     * The client's terms of service, or null.
     */
    public @Nullable String getTosUri()
    {
        return tosUri;
    }

    /**
     * The grant types the client may use ({@link GrantTypes}). A flow the client does not list is
     * refused to it.
     */
    public Set<String> getGrantTypes()
    {
        return grantTypes;
    }

    /**
     * The redirect URIs the client may be sent an authorization code to, matched exactly.
     */
    public Set<String> getRedirectUris()
    {
        return redirectUris;
    }

    /**
     * The most scopes the client may ask for, and what it gets when it names none. Empty means any
     * of the id provider's scopes.
     */
    public Set<String> getScope()
    {
        return scope;
    }

    /**
     * The most audiences the client may ask for, and what it gets when it names none. Empty means
     * any XP serves. The issuer name stands for XP as a whole.
     */
    public Set<String> getResource()
    {
        return resource;
    }

    /**
     * How long a grant to the client lives, or null for the configured default.
     */
    public @Nullable Duration getGrantTtl()
    {
        return grantTtl;
    }

    /**
     * Whether the client may obtain tokens. A disabled client gets no new grant and cannot refresh;
     * its access tokens run out on their own.
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public boolean equals( final Object o )
    {
        return this == o || o instanceof Client that && idProvider.equals( that.idProvider ) && id.equals( that.id ) &&
            displayName.equals( that.displayName ) && Objects.equals( uri, that.uri ) && Objects.equals( logoUri, that.logoUri ) &&
            Objects.equals( policyUri, that.policyUri ) && Objects.equals( tosUri, that.tosUri ) && grantTypes.equals( that.grantTypes ) &&
            redirectUris.equals( that.redirectUris ) && scope.equals( that.scope ) && resource.equals( that.resource ) &&
            Objects.equals( grantTtl, that.grantTtl ) && enabled == that.enabled;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( idProvider, id, displayName, uri, logoUri, policyUri, tosUri, grantTypes, redirectUris, scope, resource, grantTtl,
                             enabled );
    }

    @Override
    public String toString()
    {
        return "Client{" + "idProvider=" + idProvider + ", id=" + id + ", displayName=" + displayName + ", uri=" + uri + ", logoUri=" +
            logoUri + ", policyUri=" + policyUri + ", tosUri=" + tosUri + ", grantTypes=" + grantTypes + ", redirectUris=" + redirectUris +
            ", scope=" + scope + ", resource=" + resource + ", grantTtl=" + grantTtl + ", enabled=" + enabled + '}';
    }

    /**
     * Builds a client. Sets accumulate across calls; other values replace.
     */
    public static final class Builder
    {
        private @Nullable IdProviderKey idProvider;

        private @Nullable String id;

        private @Nullable String displayName;

        private @Nullable String uri;

        private @Nullable String logoUri;

        private @Nullable String policyUri;

        private @Nullable String tosUri;

        private final ImmutableSet.Builder<String> grantTypes = ImmutableSet.builder();

        private final ImmutableSet.Builder<String> redirectUris = ImmutableSet.builder();

        private final ImmutableSet.Builder<String> scope = ImmutableSet.builder();

        private final ImmutableSet.Builder<String> resource = ImmutableSet.builder();

        private @Nullable Duration grantTtl;

        private boolean enabled = true;

        private Builder()
        {
        }

        /**
         * The id provider the client belongs to. Required.
         */
        public Builder idProvider( final IdProviderKey idProvider )
        {
            this.idProvider = idProvider;
            return this;
        }

        /**
         * The client id. Required.
         */
        public Builder id( final String id )
        {
            this.id = id;
            return this;
        }

        /**
         * The name the consent page shows; the id when null.
         */
        public Builder displayName( final @Nullable String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        /**
         * The client's home page.
         */
        public Builder uri( final @Nullable String uri )
        {
            this.uri = uri;
            return this;
        }

        /**
         * The client's logo.
         */
        public Builder logoUri( final @Nullable String logoUri )
        {
            this.logoUri = logoUri;
            return this;
        }

        /**
         * The client's privacy policy.
         */
        public Builder policyUri( final @Nullable String policyUri )
        {
            this.policyUri = policyUri;
            return this;
        }

        /**
         * The client's terms of service.
         */
        public Builder tosUri( final @Nullable String tosUri )
        {
            this.tosUri = tosUri;
            return this;
        }

        /**
         * Adds grant types the client may use.
         */
        public Builder grantTypes( final Iterable<String> grantTypes )
        {
            this.grantTypes.addAll( grantTypes );
            return this;
        }

        /**
         * Adds redirect URIs.
         */
        public Builder redirectUris( final Iterable<String> redirectUris )
        {
            this.redirectUris.addAll( redirectUris );
            return this;
        }

        /**
         * Adds scopes to the most the client may ask for.
         *
         * @throws IllegalArgumentException when a scope is not a {@link ScopeNames scope name}
         */
        public Builder scope( final Iterable<String> scope )
        {
            scope.forEach( s -> this.scope.add( ScopeNames.checkScope( s ) ) );
            return this;
        }

        /**
         * Adds audiences to the most the client may ask for.
         */
        public Builder resource( final Iterable<String> resource )
        {
            this.resource.addAll( resource );
            return this;
        }

        /**
         * How long a grant to the client lives; null (the default) for the configured default.
         */
        public Builder grantTtl( final @Nullable Duration grantTtl )
        {
            this.grantTtl = grantTtl;
            return this;
        }

        /**
         * Whether the client may obtain tokens; true by default.
         */
        public Builder enabled( final boolean enabled )
        {
            this.enabled = enabled;
            return this;
        }

        /**
         * Whether XP seeded the client; false by default.
         */
        /**
         * Builds the client.
         *
         * @throws NullPointerException     when the id provider or the id is missing
         * @throws IllegalArgumentException when a grant type is unknown, a URI is not absolute, a link is not an http or https URL,
         *                                  a scope is not a scope name, or the grant lifetime is not positive
         */
        public Client build()
        {
            return new Client( this );
        }
    }
}
