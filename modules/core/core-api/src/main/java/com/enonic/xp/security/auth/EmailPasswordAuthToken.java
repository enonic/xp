package com.enonic.xp.security.auth;

import com.google.common.annotations.Beta;

import com.enonic.xp.mail.EmailValidator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Beta
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
        checkNotNull( email, "Email can not be null" );
        checkArgument( EmailValidator.validate( email ), "Email [" + email + "] is not valid" );
        this.email = email;
    }
}
