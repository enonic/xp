package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.RoleRestriction;

import static java.util.Objects.requireNonNull;

/**
 * What a caller presents to {@link com.enonic.xp.security.SecurityService#authenticate}. A token
 * names the id provider and identifies the user, either by a credential XP checks or by an
 * identity the caller has already verified. The result is an {@link AuthenticationInfo} built from
 * the user's current memberships, which the token may {@link #getRestriction() restrict} but never
 * widen.
 * <p>
 * The hierarchy is sealed: the security service handles every token type.
 */
@NullMarked
public abstract sealed class AuthenticationToken
    permits PasswordAuthToken, VerifiedEmailAuthToken, VerifiedUsernameAuthToken, VerifiedUserKeyAuthToken
{
    private final IdProviderKey idProvider;

    private final RoleRestriction restriction;

    /**
     * A token with no restriction.
     *
     * @param idProvider the id provider the user belongs to
     */
    protected AuthenticationToken( final IdProviderKey idProvider )
    {
        this( idProvider, RoleRestriction.none() );
    }

    /**
     * A token with a restriction.
     *
     * @param idProvider  the id provider the user belongs to
     * @param restriction the restriction on the roles the authentication holds
     */
    protected AuthenticationToken( final IdProviderKey idProvider, final RoleRestriction restriction )
    {
        this.idProvider = requireNonNull( idProvider );
        this.restriction = requireNonNull( restriction );
    }

    /**
     * The id provider the user is looked up in.
     */
    public final IdProviderKey getIdProvider()
    {
        return this.idProvider;
    }

    /**
     * The restriction on the roles the resulting authentication holds.
     */
    public final RoleRestriction getRestriction()
    {
        return this.restriction;
    }
}
