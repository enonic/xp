package com.enonic.xp.security.auth;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.mail.EmailValidator;

@PublicApi
public final class EmailPasswordAuthToken
    extends PasswordAuthToken
{
    private String email;

    public String getEmail()
    {
        return this.email;
    }

    public void setEmail( final String email )
    {
        Objects.requireNonNull( email, "email is required" );
        Preconditions.checkArgument( EmailValidator.isValid( email ), "Email [%s] is not valid", email );
        this.email = email;
    }
}
