package com.enonic.xp.security;

import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.mail.EmailValidator;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public final class User
    extends Principal
{
    @Deprecated
    public static final User ANONYMOUS = anonymous();

    private final String email;

    private final String login;

    private final String authenticationHash;

    private final boolean loginDisabled;

    private final PropertyTree profile;

    private User( final Builder builder )
    {
        super( builder );

        this.email = builder.email;
        this.login = builder.login;
        this.loginDisabled = builder.loginDisabled;
        this.authenticationHash = builder.authenticationHash;
        this.profile = builder.profile;
    }

    public String getEmail()
    {
        return email;
    }

    public String getLogin()
    {
        return login;
    }

    public String getAuthenticationHash()
    {
        return authenticationHash;
    }

    public boolean isDisabled()
    {
        return loginDisabled;
    }

    public PropertyTree getProfile()
    {
        return profile;
    }

    public static User anonymous()
    {
        return User.create().key( PrincipalKey.ofAnonymous() ).displayName( "Anonymous User" ).login( "anonymous" ).build();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof User ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }
        final User user = (User) o;
        return loginDisabled == user.loginDisabled && Objects.equals( email, user.email ) && Objects.equals( login, user.login ) &&
            Objects.equals( authenticationHash, user.authenticationHash ) && Objects.equals( profile, user.profile );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), email, login, authenticationHash, loginDisabled, profile );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final User user )
    {
        return new Builder( user );
    }

    public static final class Builder
        extends Principal.Builder<Builder>
    {
        private String email;

        private String login;

        private String authenticationHash;

        private boolean loginDisabled;

        private PropertyTree profile;

        private Builder()
        {
            super();
            this.profile = new PropertyTree();
        }

        private Builder( final User user )
        {
            super( user );
            this.email = user.getEmail();
            this.login = user.getLogin();
            this.authenticationHash = user.getAuthenticationHash();
            this.loginDisabled = user.isDisabled();
            this.profile = user.profile == null ? new PropertyTree() : user.profile.copy();
        }

        public Builder login( final String value )
        {
            this.login = value;
            if ( this.displayName == null )
            {
                this.displayName = login;
            }
            return this;
        }

        public Builder email( final String value )
        {
            this.email = Strings.emptyToNull( value );
            return this;
        }

        public Builder authenticationHash( final String value )
        {
            this.authenticationHash = value;
            return this;
        }

        public Builder profile( final PropertyTree profile )
        {
            this.profile = profile;
            return this;
        }

        public Builder disabled( final boolean loginDisabled )
        {
            this.loginDisabled = loginDisabled;
            return this;
        }

        @Override
        protected void validate()
        {
            super.validate();
            Objects.requireNonNull( this.login, "login is required for a User" );
            Preconditions.checkArgument( this.key.isUser(), "Invalid Principal Type for User: %s", this.key.getType() );
            if ( !isNullOrEmpty( this.email ) )
            {
                Preconditions.checkArgument( EmailValidator.isValid( this.email ), "Email [%s] is not valid", this.email );
            }
        }

        public User build()
        {
            validate();
            return new User( this );
        }
    }
}
