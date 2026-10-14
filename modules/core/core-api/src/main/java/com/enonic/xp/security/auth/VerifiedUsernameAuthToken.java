package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalRestriction;


/**
 * Vouches for a user identified by login name: the caller has verified the identity by other
 * means, such as an upstream identity provider, and XP checks no credential. Only a caller
 * that has done so may use it.
 */
@NullMarked
public final class VerifiedUsernameAuthToken
    extends AuthenticationToken
{
    private final String username;

    /**
     * A token with no principal restriction.
     *
     * @param idProvider the id provider the user belongs to
     * @param username   the login name of the user
     */
    public VerifiedUsernameAuthToken( final IdProviderKey idProvider, final String username )
    {
        this( idProvider, username, PrincipalRestriction.none() );
    }

    /**
     * A token with a restriction on the memberships the authentication holds.
     *
     * @param idProvider the id provider the user belongs to
     * @param username   the login name of the user
     * @param restriction the restriction on the memberships the authentication holds. See
     *                    {@link AuthenticationToken#getRestriction()}.
     */
    public VerifiedUsernameAuthToken( final IdProviderKey idProvider, final String username, final PrincipalRestriction restriction )
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
