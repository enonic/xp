package com.enonic.xp.security;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.mail.EmailValidator;

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
        this.key = Objects.requireNonNull( builder.principalKey, "userKey is required for a user" );
        this.displayName = Objects.requireNonNull( builder.displayName, "displayName is required for a user" );
        if ( builder.email != null )
        {
            Preconditions.checkArgument( EmailValidator.isValid( builder.email ), "Email [%s] is not valid", builder.email );
        }
        this.email = builder.email;
        this.login = Objects.requireNonNull( builder.login, "login is required for a user" );
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

    public static final class Builder
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
            Preconditions.checkArgument( value.isUser(), "Invalid PrincipalType for user key: %s", value.getType() );
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
