package com.enonic.xp.security;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.mail.EmailValidator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

@Beta
public final class User
    extends Principal
{

    public final static User ANONYMOUS = User.create().
        key( PrincipalKey.ofAnonymous() ).
        displayName( "Anonymous User" ).
        login( "anonymous" ).
        build();

    private final String email;

    private final String login;

    private final String authenticationHash;

    private final boolean loginDisabled;

    private final PropertyTree profile;

    private User( final Builder builder )
    {
        super( builder );
        checkNotNull( builder.login, "login is required for a User" );

        if ( !Strings.isNullOrEmpty( builder.email ) )
        {
            checkArgument( EmailValidator.isValid( builder.email ), "Email [" + builder.email + "] is not valid" );
        }

        this.email = builder.email;
        this.login = requireNonNull( builder.login );
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

    public static class Builder
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
            Preconditions.checkArgument( this.key.isUser(), "Invalid Principal Type for User: " + this.key.getType() );
        }

        public User build()
        {
            return new User( this );
        }
    }
}
