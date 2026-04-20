package com.enonic.xp.security.auth;

import org.jspecify.annotations.NullMarked;

import com.google.common.base.Preconditions;

import com.enonic.xp.mail.EmailValidator;
import com.enonic.xp.security.IdProviderKey;

import static java.util.Objects.requireNonNull;


@NullMarked
public final class VerifiedEmailAuthToken
    extends AuthenticationToken
{
    private final String email;

    public VerifiedEmailAuthToken( final IdProviderKey idProvider, final String email )
    {
        super( idProvider );
        this.email = checkValidEmail( email );
    }

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
