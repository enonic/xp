package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.google.common.base.Preconditions;

import com.enonic.xp.mail.EmailValidator;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalRestriction;

import static java.util.Objects.requireNonNull;


/**
 * Vouches for a user identified by email: the caller has verified the identity by other means,
 * such as an upstream identity provider, and XP checks no credential. Only a caller that has
 * done so may use it. The email must be a valid address.
 */
@NullMarked
public final class VerifiedEmailAuthToken
    extends AuthenticationToken
{
    private final String email;

    /**
     * A token with no principal restriction.
     *
     * @param idProvider the id provider the user belongs to
     * @param email      the email of the user, a valid address
     */
    public VerifiedEmailAuthToken( final IdProviderKey idProvider, final String email )
    {
        this( idProvider, email, PrincipalRestriction.none() );
    }

    /**
     * A token with a restriction on the memberships the authentication holds.
     *
     * @param idProvider the id provider the user belongs to
     * @param email      the email of the user, a valid address
     * @param restriction the restriction on the memberships the authentication holds. See
     *                    {@link AuthenticationToken#getRestriction()}.
     */
    public VerifiedEmailAuthToken( final IdProviderKey idProvider, final String email, final PrincipalRestriction restriction )
    {
        super( idProvider, restriction );
        this.email = checkValidEmail( email );
    }

    /**
     * The email the user is found by.
     */
    public String getEmail()
    {
        return this.email;
    }

    private static String checkValidEmail( final String email )
    {
        requireNonNull( email, "email is required" );
        Preconditions.checkArgument( EmailValidator.isValid( email ), "Email [%s] is not valid", email );
        return email;
    }
}
