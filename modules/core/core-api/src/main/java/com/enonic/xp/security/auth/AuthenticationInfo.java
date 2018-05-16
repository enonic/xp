package com.enonic.xp.security.auth;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;

import static com.google.common.base.Preconditions.checkNotNull;

@Beta
public final class AuthenticationInfo
    implements Serializable
{
    private static final long serialVersionUID = 5464920698278527343L;

    private transient User user;

    private transient PrincipalKeys principals;

    private transient boolean authenticated;

    private AuthenticationInfo( final Builder builder )
    {
        this.authenticated = builder.authenticated;
        if ( builder.authenticated )
        {
            this.user = checkNotNull( builder.user, "AuthenticationInfo user cannot be null" );
            builder.principals.add( user.getKey() );
        }
        else
        {
            this.user = null;
        }
        this.principals = PrincipalKeys.from( builder.principals.build() );
    }

    public boolean isAuthenticated()
    {
        return authenticated;
    }

    public User getUser()
    {
        return user;
    }

    public PrincipalKeys getPrincipals()
    {
        return principals;
    }

    public boolean hasRole( final String role )
    {
        return principals.stream().anyMatch( principal -> principal.isRole() && principal.getId().equals( role ) );
    }

    public boolean hasRole( final PrincipalKey role )
    {
        return principals.stream().anyMatch( principal -> principal.isRole() && principal.equals( role ) );
    }

    public static Builder create()
    {
        return new Builder( true );
    }

    public static Builder copyOf( final AuthenticationInfo authInfo )
    {
        return new Builder( authInfo );
    }

    public static AuthenticationInfo unAuthenticated()
    {
        return new Builder( false ).principals( PrincipalKey.ofAnonymous(), RoleKeys.EVERYONE ).build();
    }

    private void readObject( ObjectInputStream ois )
        throws ClassNotFoundException, IOException
    {
        this.authenticated = ois.readBoolean();
        this.principals = PrincipalKeys.from( ois.readUTF().split( "," ) );
        this.user = deserializeUser( ois );
    }

    private void writeObject( ObjectOutputStream oos )
        throws IOException
    {
        oos.writeBoolean( authenticated );
        String principalKeys = principals.stream().map( PrincipalKey::toString ).collect( Collectors.joining( "," ) );
        oos.writeUTF( principalKeys );
        serializeUser( oos, this.user );
    }

    private void serializeUser( final ObjectOutputStream oos, final User user )
        throws IOException
    {
        oos.writeUTF( user.getKey().toString() );
        oos.writeObject( user.getDisplayName() );
        oos.writeObject( user.getModifiedTime() );
        oos.writeObject( user.getEmail() );
        oos.writeUTF( user.getLogin() );
        oos.writeBoolean( user.isDisabled() );
        oos.writeObject( user.getProfile() );
    }

    private User deserializeUser( final ObjectInputStream ois )
        throws IOException, ClassNotFoundException
    {
        User.Builder user = User.create();
        user.key( PrincipalKey.from( ois.readUTF() ) );
        user.displayName( (String) ois.readObject() );
        user.modifiedTime( (Instant) ois.readObject() );
        user.email( (String) ois.readObject() );
        user.login( ois.readUTF() );
        user.disabled( ois.readBoolean() );
        user.profile( (PropertyTree) ois.readObject() );
        return user.build();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final AuthenticationInfo that = (AuthenticationInfo) o;
        return authenticated == that.authenticated && Objects.equals( user, that.user ) && Objects.equals( principals, that.principals );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( user, principals, authenticated );
    }

    public static class Builder
    {
        private User user;

        private final ImmutableSet.Builder<PrincipalKey> principals;

        private boolean authenticated;

        private Builder( final boolean authenticated )
        {
            this.principals = ImmutableSet.builder();
            this.authenticated = authenticated;
        }

        private Builder( final AuthenticationInfo authInfo )
        {
            this.principals = ImmutableSet.builder();
            this.user = authInfo.getUser();
            this.authenticated = authInfo.isAuthenticated();
            this.principals.addAll( authInfo.getPrincipals() );
        }

        public Builder user( final User user )
        {
            this.user = user;
            return this;
        }

        public Builder principals( final Iterable<PrincipalKey> principals )
        {
            this.principals.addAll( principals );
            return this;
        }

        public Builder principals( final PrincipalKey... principals )
        {
            for ( PrincipalKey principal : principals )
            {
                this.principals.add( principal );
            }
            return this;
        }

        public AuthenticationInfo build()
        {
            return new AuthenticationInfo( this );
        }
    }

}
