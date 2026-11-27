package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleRestriction;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Identifies the user by key without checking a credential: the caller has verified the identity
 * by other means, as with a verified access token's subject. The id provider is the key's.
 */
@NullMarked
public final class VerifiedUserKeyAuthToken
    extends AuthenticationToken
{
    private final PrincipalKey userKey;

    /**
     * A token with no restriction.
     *
     * @param userKey the user's key
     */
    public VerifiedUserKeyAuthToken( final PrincipalKey userKey )
    {
        this( userKey, RoleRestriction.none() );
    }

    /**
     * A token with a restriction.
     *
     * @param userKey     the user's key
     * @param restriction the restriction on the roles the authentication holds
     * @throws IllegalArgumentException if the key is not a user's
     */
    public VerifiedUserKeyAuthToken( final PrincipalKey userKey, final RoleRestriction restriction )
    {
        requireNonNull( userKey, "userKey is required" );
        checkArgument( userKey.isUser(), "Expected a user key: %s", userKey );
        super( userKey.getIdProviderKey(), restriction );
        this.userKey = userKey;
    }

    /**
     * The key of the user to authenticate.
     */
    public PrincipalKey getUserKey()
    {
        return this.userKey;
    }
}
