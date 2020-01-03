package com.enonic.xp.security;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.mail.EmailValidator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@PublicApi
public final class CreateUserParams
{
    private final PrincipalKey key;

    private final String displayName;

    private final String email;

    private final String login;

    private final String password;

    private CreateUserParams( final Builder builder )
    {
        this.key = checkNotNull( builder.principalKey, "userKey is required for a user" );
        this.displayName = checkNotNull( builder.displayName, "displayName is required for a user" );
        if ( builder.email != null )
        {
            checkArgument( EmailValidator.isValid( builder.email ), "Email [" + builder.email + "] is not valid" );
        }
        this.email = builder.email;
        this.login = checkNotNull( builder.login, "login is required for a user" );
        this.password = builder.password;
    }

    public PrincipalKey getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getPassword()
    {
        return password;
    }

    public String getEmail()
    {
        return email;
    }

    public String getLogin()
    {
        return login;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private PrincipalKey principalKey;

        private String displayName;

        private String email;

        private String login;

        private String password;

        private Builder()
        {
        }

        public Builder userKey( final PrincipalKey value )
        {
            Preconditions.checkArgument( value.isUser(), "Invalid PrincipalType for user key: " + value.getType() );
            this.principalKey = value;
            return this;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder login( final String value )
        {
            this.login = value;
            return this;
        }

        public Builder email( final String value )
        {
            this.email = value;
            return this;
        }

        public Builder password( final String value )
        {
            this.password = value;
            return this;
        }

        public CreateUserParams build()
        {
            return new CreateUserParams( this );
        }
    }
}
