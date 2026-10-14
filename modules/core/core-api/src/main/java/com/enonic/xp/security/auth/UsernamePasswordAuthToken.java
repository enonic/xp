package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalRestriction;


/**
 * Identifies the user by login name and checks the password.
 */
@NullMarked
public final class UsernamePasswordAuthToken
    extends PasswordAuthToken
{
    private final String username;

    /**
     * A token with no principal restriction.
     *
     * @param idProvider the id provider the user belongs to
     * @param username   the login name of the user
     * @param password   the password to check, never null
     */
    public UsernamePasswordAuthToken( final IdProviderKey idProvider, final String username, final String password )
    {
        this( idProvider, username, password, PrincipalRestriction.none() );
    }

    /**
     * A token with a restriction on the memberships the authentication holds.
     *
     * @param idProvider the id provider the user belongs to
     * @param username   the login name of the user
     * @param password   the password to check, never null
     * @param restriction the restriction on the memberships the authentication holds. See
     *                    {@link AuthenticationToken#getRestriction()}.
     */
    public UsernamePasswordAuthToken( final IdProviderKey idProvider, final String username, final String password,
                                      final PrincipalRestriction restriction )
    {
        super( idProvider, password, restriction );
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
