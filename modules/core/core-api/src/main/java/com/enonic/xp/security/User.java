package com.enonic.xp.security;

import java.util.Map;
import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.data.PropertySet;
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

    private final ImmutableMap<String, PropertySet> extraDataMap;

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
        this.extraDataMap = ImmutableMap.copyOf( builder.extraDataMap );
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

    public ImmutableMap<String, PropertySet> getExtraDataMap()
    {
        return extraDataMap;
    }

    public PropertySet getExtraData( final String namespace )
    {
        return extraDataMap.get( sanitizeNamespace( namespace ) );
    }

    public static String sanitizeNamespace( String namespace )
    {
        return namespace.replace( '.', '-' );
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

        private Map<String, PropertySet> extraDataMap = Maps.newHashMap();

        private Builder()
        {
            super();
        }

        private Builder( final User user )
        {
            super( user );
            this.email = user.getEmail();
            this.login = user.getLogin();
            this.authenticationHash = user.getAuthenticationHash();
            this.loginDisabled = user.isDisabled();
            this.extraDataMap.putAll( user.extraDataMap );
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

        public Builder putExtraData( final String namespace, PropertySet extraData )
        {
            this.extraDataMap.put( sanitizeNamespace( namespace ), extraData );
            return this;
        }

        public Builder putAllExtraDataMap( final Map<String, PropertySet> extraDataMap )
        {
            extraDataMap.entrySet().
                forEach( entry -> this.putExtraData( entry.getKey(), entry.getValue() ) );
            return this;
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

            final User other = (User) o;

            return super.equals( o ) &&
                Objects.equals( email, other.email ) &&
                Objects.equals( authenticationHash, other.authenticationHash ) &&
                Objects.equals( login, other.login ) &&
                Objects.equals( loginDisabled, other.loginDisabled ) &&
                Objects.equals( ImmutableMap.copyOf( extraDataMap ), other.extraDataMap );

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
