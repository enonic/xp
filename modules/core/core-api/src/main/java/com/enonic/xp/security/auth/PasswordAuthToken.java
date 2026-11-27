package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.RoleRestriction;

import static java.util.Objects.requireNonNull;

/**
 * Identifies the user by a credential XP checks: the password, against the hash stored on the
 * user. An empty password never authenticates.
 */
@NullMarked
public abstract sealed class PasswordAuthToken
    extends AuthenticationToken
    permits EmailPasswordAuthToken, UsernamePasswordAuthToken
{
    private final String password;

    /**
     * A token with no restriction.
     *
     * @param idProvider the id provider the user belongs to
     * @param password   the password to check
     */
    protected PasswordAuthToken( final IdProviderKey idProvider, final String password )
    {
        this( idProvider, password, RoleRestriction.none() );
    }

    /**
     * A token with a restriction.
     *
     * @param idProvider  the id provider the user belongs to
     * @param password    the password to check
     * @param restriction the restriction on the roles the authentication holds
     */
    protected PasswordAuthToken( final IdProviderKey idProvider, final String password, final RoleRestriction restriction )
    {
        super( idProvider, restriction );
        this.password = requireNonNull( password, "password cannot be null" );
    }

    /**
     * The password to check. A credential: never log it.
     */
    public final String getPassword()
    {
        return this.password;
    }
}
