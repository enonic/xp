package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.google.common.base.Preconditions;

import com.enonic.xp.mail.EmailValidator;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.RoleRestriction;

import static java.util.Objects.requireNonNull;


/**
 * Identifies the user by email and checks the password. The email must be a valid address.
 */
@NullMarked
public final class EmailPasswordAuthToken
    extends PasswordAuthToken
{
    private final String email;

    /**
     * A token with no restriction.
     *
     * @param idProvider the id provider the user belongs to
     * @param email      the user's email, a valid address
     * @param password   the password to check
     */
    public EmailPasswordAuthToken( final IdProviderKey idProvider, final String email, final String password )
    {
        this( idProvider, email, password, RoleRestriction.none() );
    }

    /**
     * A token with a restriction.
     *
     * @param idProvider  the id provider the user belongs to
     * @param email       the user's email, a valid address
     * @param password    the password to check
     * @param restriction the restriction on the roles the authentication holds
     */
    public EmailPasswordAuthToken( final IdProviderKey idProvider, final String email, final String password,
                                   final RoleRestriction restriction )
    {
        super( idProvider, password, restriction );
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
