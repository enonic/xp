package com.enonic.xp.security.token;

import java.time.Duration;
import java.util.Set;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * A client as a {@link ClientEditor} sees it: every value that may change, as a field, starting
 * out as the client has it. The id provider and the id do not change.
 */
@NullMarked
public final class EditableClient
{
    /**
     * The client as it is before the edit.
     */
    public final Client source;

    /**
     * The name the consent page shows; null falls back to the id.
     */
    public @Nullable String displayName;

    /**
     * The client's home page; null for none.
     */
    public @Nullable String uri;

    /**
     * The client's logo; null for none.
     */
    public @Nullable String logoUri;

    /**
     * The client's privacy policy; null for none.
     */
    public @Nullable String policyUri;

    /**
     * The client's terms of service; null for none.
     */
    public @Nullable String tosUri;

    /**
     * The grant types the client may use; a flow not listed is refused to it.
     */
    public Set<String> grantTypes;

    /**
     * Where the authorization code flow may send the browser; empty for nowhere.
     */
    public Set<String> redirectUris;

    /**
     * The most scopes the client may ask for; empty for any.
     */
    public Set<String> scope;

    /**
     * The most audiences the client may ask for; empty for any.
     */
    public Set<String> resource;

    /**
     * How long a grant to the client lives; null for the configured default.
     */
    public @Nullable Duration grantTtl;

    /**
     * Whether the client may obtain tokens.
     */
    public boolean enabled;

    /**
     * Starts out with the values of the client.
     */
    public EditableClient( final Client source )
    {
        this.source = source;
        this.displayName = source.getDisplayName();
        this.uri = source.getUri();
        this.logoUri = source.getLogoUri();
        this.policyUri = source.getPolicyUri();
        this.tosUri = source.getTosUri();
        this.grantTypes = source.getGrantTypes();
        this.redirectUris = source.getRedirectUris();
        this.scope = source.getScope();
        this.resource = source.getResource();
        this.grantTtl = source.getGrantTtl();
        this.enabled = source.isEnabled();
    }

    /**
     * @throws IllegalArgumentException when a value is invalid, see {@link Client.Builder#build()}
     */
    public Client build()
    {
        return Client.create()
            .idProvider( source.getIdProvider() )
            .id( source.getId() )
            .displayName( displayName )
            .uri( uri )
            .logoUri( logoUri )
            .policyUri( policyUri )
            .tosUri( tosUri )
            .grantTypes( grantTypes )
            .redirectUris( redirectUris )
            .scope( scope )
            .resource( resource )
            .grantTtl( grantTtl )
            .enabled( enabled )
            .build();
    }
}
