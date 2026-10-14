package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalRestriction;

import static java.util.Objects.requireNonNull;

/**
 * What a caller presents to {@link com.enonic.xp.security.SecurityService#authenticate} to
 * authenticate a user of an id provider. A token names the id provider and identifies the user
 * in one of the ways the subclasses define: by credentials XP checks, or by an identity the
 * caller has already verified and vouches for. Either way, the result is an
 * {@link AuthenticationInfo} built from the user's memberships as they are at that moment, which
 * the token may {@link #getRestriction() restrict} but never widen.
 * <p>
 * The hierarchy is sealed: the security service handles every token type, and an id provider
 * chooses a type rather than defining one.
 */
@NullMarked
public abstract sealed class AuthenticationToken
    permits PasswordAuthToken, VerifiedEmailAuthToken, VerifiedUsernameAuthToken, VerifiedUserKeyAuthToken
{
    private final IdProviderKey idProvider;

    private final PrincipalRestriction restriction;

    /**
     * A token for the id provider, with no principal restriction.
     *
     * @param idProvider the id provider the user belongs to
     */
    protected AuthenticationToken( final IdProviderKey idProvider )
    {
        this( idProvider, PrincipalRestriction.none() );
    }

    /**
     * A token for the id provider.
     *
     * @param idProvider the id provider the user belongs to
     * @param restriction the restriction on the memberships the authentication holds. See
     *                    {@link #getRestriction()}.
     */
    protected AuthenticationToken( final IdProviderKey idProvider, final PrincipalRestriction restriction )
    {
        this.idProvider = requireNonNull( idProvider );
        this.restriction = requireNonNull( restriction );
    }

    /**
     * The id provider the user is looked up in. The resulting authentication belongs to this id
     * provider, whatever the vhost's default.
     */
    public final IdProviderKey getIdProvider()
    {
        return this.idProvider;
    }

    /**
     * The restriction on the memberships the resulting authentication holds. A ceiling, not a
     * grant: the authenticated context holds the restriction applied to the user's current
     * memberships, plus the user's own key and the roles every authenticated user has, so a
     * role the user does not hold, or no longer holds, is never gained through it.
     */
    public final PrincipalRestriction getRestriction()
    {
        return this.restriction;
    }
}
