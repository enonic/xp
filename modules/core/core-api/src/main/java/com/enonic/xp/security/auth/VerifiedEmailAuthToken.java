package com.enonic.xp.security.auth;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.mail.EmailValidator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@PublicApi
public final class VerifiedEmailAuthToken
    extends AuthenticationToken
{
    private String email;

    public String getEmail()
    {
        return this.email;
    }

    public void setEmail( final String email )
    {
        checkNotNull( email, "Email cannot be null" );
        checkArgument( EmailValidator.isValid( email ), "Email [" + email + "] is not valid" );
        this.email = email;
    }
}
