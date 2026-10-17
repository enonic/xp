package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalRestriction;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Vouches for a user identified by key: the caller has verified the identity by other means and
 * XP checks no credential. This is the form a verified access token's subject, or an actor
 * acting for a user, comes in, so no lookup by login or email is needed. The id provider is the
 * key's. Only a caller that has verified the identity may use it.
 */
@NullMarked
public final class VerifiedUserKeyAuthToken
    extends AuthenticationToken
{
    private final PrincipalKey userKey;

    /**
     * A token with no principal restriction.
     *
     * @param userKey the key of a user; its id provider is the token's
     */
    public VerifiedUserKeyAuthToken( final PrincipalKey userKey )
    {
        this( userKey, PrincipalRestriction.none() );
    }

    /**
     * A token with a restriction on the memberships the authentication holds.
     *
     * @param userKey    the key of a user; its id provider is the token's
     * @param restriction the restriction on the memberships the authentication holds. See
     *                    {@link AuthenticationToken#getRestriction()}.
     * @throws IllegalArgumentException when the key is not a user's
     */
    public VerifiedUserKeyAuthToken( final PrincipalKey userKey, final PrincipalRestriction restriction )
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
