package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.RoleRestriction;


/**
 * Identifies the user by login name without checking a credential: the caller has verified the
 * identity by other means, such as an upstream identity provider.
 */
@NullMarked
public final class VerifiedUsernameAuthToken
    extends AuthenticationToken
{
    private final String username;

    /**
     * A token with no restriction.
     *
     * @param idProvider the id provider the user belongs to
     * @param username   the user's login name
     */
    public VerifiedUsernameAuthToken( final IdProviderKey idProvider, final String username )
    {
        this( idProvider, username, RoleRestriction.none() );
    }

    /**
     * A token with a restriction.
     *
     * @param idProvider  the id provider the user belongs to
     * @param username    the user's login name
     * @param restriction the restriction on the roles the authentication holds
     */
    public VerifiedUsernameAuthToken( final IdProviderKey idProvider, final String username, final RoleRestriction restriction )
    {
        super( idProvider, restriction );
        this.username = username;
    }

    /**
     * The login name the user is found by.
     */
    public String getUsername()
    {
        return this.username;
    }
}
