package com.enonic.xp.security.auth;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleRestriction;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;

import static java.util.Objects.requireNonNull;


public final class AuthenticationInfo
    implements Serializable
{
    @Serial
    private static final long serialVersionUID = 5464920698278527343L;

    private transient User user;

    private transient PrincipalKeys principals;

    private transient boolean authenticated;

    private transient PrincipalKey actor;

    private transient RoleRestriction restriction;

    private AuthenticationInfo( final Builder builder )
    {
        this.authenticated = builder.authenticated;
        if ( builder.authenticated )
        {
            this.user = requireNonNull( builder.user, "user is required" );
            builder.principals.add( user.getKey() );
            this.actor = builder.actor;
            this.restriction = builder.restriction;
        }
        else
        {
            this.user = null;
            this.actor = null;
            this.restriction = RoleRestriction.none();
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

    /**
     * The restriction this authentication was established under, or none. The principals already
     * have it applied.
     */
    public RoleRestriction getRestriction()
    {
        return restriction;
    }

    /**
     * The principal acting on the user's behalf, such as a service making a call for them, or null
     * if the user acts for themselves. Recorded in the audit log next to the user.
     */
    public @Nullable PrincipalKey getActor()
    {
        return actor;
    }

    public boolean hasRole( final String role )
    {
        return principals.stream().anyMatch( principal -> principal.isRole() && principal.getId().equals( role ) );
    }

    public boolean hasRole( final PrincipalKey role )
    {
        if ( !role.isRole() )
        {
            return false;
        }
        return principals.contains( role );
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

    @Serial
    private void readObject( ObjectInputStream ois )
        throws ClassNotFoundException, IOException
    {
        this.authenticated = ois.readBoolean();
        this.principals = PrincipalKeys.from( ois.readUTF().split( "," ) );
        final User deserializedUser = deserializeUser( ois );
        this.user = this.authenticated ? deserializedUser : null;
        this.actor = this.authenticated ? deserializeActor( ois ) : null;
        this.restriction = this.authenticated ? deserializeRestriction( ois ) : RoleRestriction.none();
    }

    @Serial
    private void writeObject( ObjectOutputStream oos )
        throws IOException
    {
        oos.writeBoolean( authenticated );
        String principalKeys = principals.stream().map( PrincipalKey::toString ).collect( Collectors.joining( "," ) );
        oos.writeUTF( principalKeys );
        serializeUser( oos, authenticated ? this.user : User.anonymous() );
        oos.writeUTF( actor == null ? "" : actor.toString() );
        oos.writeBoolean( restriction.isRestricted() );
        oos.writeUTF( restriction instanceof RoleRestriction.To( PrincipalKeys keys )
                          ? keys.stream().map( PrincipalKey::toString ).collect( Collectors.joining( "," ) )
                          : "" );
    }

    /**
     * The actor and the restriction were added later, in that order. A stream written before them
     * ends early and reads without them.
     */
    private static PrincipalKey deserializeActor( final ObjectInputStream ois )
        throws IOException
    {
        final String actor;
        try
        {
            actor = ois.readUTF();
        }
        catch ( EOFException e )
        {
            return null;
        }
        return actor.isEmpty() ? null : PrincipalKey.from( actor );
    }

    private static RoleRestriction deserializeRestriction( final ObjectInputStream ois )
        throws IOException
    {
        final boolean restricted;
        final String keys;
        try
        {
            restricted = ois.readBoolean();
            keys = ois.readUTF();
        }
        catch ( EOFException e )
        {
            return RoleRestriction.none();
        }
        return restricted
            ? RoleRestriction.to( keys.isEmpty() ? PrincipalKeys.empty() : PrincipalKeys.from( keys.split( "," ) ) )
            : RoleRestriction.none();
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
        return authenticated == that.authenticated && Objects.equals( user, that.user ) && Objects.equals( principals, that.principals ) &&
            Objects.equals( actor, that.actor ) && Objects.equals( restriction, that.restriction );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( user, principals, authenticated, actor, restriction );
    }

    public static final class Builder
    {
        private User user;

        private final ImmutableSet.Builder<PrincipalKey> principals;

        private final boolean authenticated;

        private PrincipalKey actor;

        private RoleRestriction restriction = RoleRestriction.none();

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
            this.actor = authInfo.getActor();
            this.restriction = authInfo.getRestriction();
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

        /**
         * The principal acting on the user's behalf, or null when the user acts for themselves.
         */
        public Builder actor( final @Nullable PrincipalKey actor )
        {
            this.actor = actor;
            return this;
        }

        /**
         * The restriction the authentication is established under; none by default. It is recorded,
         * not applied: the principals are stored as given.
         */
        public Builder restriction( final RoleRestriction restriction )
        {
            this.restriction = requireNonNull( restriction );
            return this;
        }

        public AuthenticationInfo build()
        {
            return new AuthenticationInfo( this );
        }
    }

}
