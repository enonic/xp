package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalRestriction;

import static java.util.Objects.requireNonNull;

/**
 * A token that identifies the user by a credential XP checks: the password, against the hash
 * stored on the user. An empty password never authenticates. The subclasses say how the user is
 * found, by login name or by email.
 */
@NullMarked
public abstract sealed class PasswordAuthToken
    extends AuthenticationToken
    permits EmailPasswordAuthToken, UsernamePasswordAuthToken
{
    private final String password;

    /**
     * A token with the password and no principal restriction.
     *
     * @param idProvider the id provider the user belongs to
     * @param password   the password to check, never null
     */
    protected PasswordAuthToken( final IdProviderKey idProvider, final String password )
    {
        this( idProvider, password, PrincipalRestriction.none() );
    }

    /**
     * A token with the password.
     *
     * @param idProvider the id provider the user belongs to
     * @param password   the password to check, never null
     * @param restriction the restriction on the memberships the authentication holds. See
     *                    {@link AuthenticationToken#getRestriction()}.
     */
    protected PasswordAuthToken( final IdProviderKey idProvider, final String password, final PrincipalRestriction restriction )
    {
        super( idProvider, restriction );
        this.password = requireNonNull( password, "password cannot be null" );
    }

    /**
     * The password to check against the user's stored hash. A credential: not to be logged.
     */
    public final String getPassword()
    {
        return this.password;
    }
}
