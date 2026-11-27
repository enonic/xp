package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.RoleRestriction;


/**
 * Identifies the user by login name and checks the password.
 */
@NullMarked
public final class UsernamePasswordAuthToken
    extends PasswordAuthToken
{
    private final String username;

    /**
     * A token with no restriction.
     *
     * @param idProvider the id provider the user belongs to
     * @param username   the user's login name
     * @param password   the password to check
     */
    public UsernamePasswordAuthToken( final IdProviderKey idProvider, final String username, final String password )
    {
        this( idProvider, username, password, RoleRestriction.none() );
    }

    /**
     * A token with a restriction.
     *
     * @param idProvider  the id provider the user belongs to
     * @param username    the user's login name
     * @param password    the password to check
     * @param restriction the restriction on the roles the authentication holds
     */
    public UsernamePasswordAuthToken( final IdProviderKey idProvider, final String username, final String password,
                                      final RoleRestriction restriction )
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
