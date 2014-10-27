package com.enonic.wem.core.security;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;

import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalQuery;
import com.enonic.wem.api.security.PrincipalQueryResult;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStores;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.security.auth.AuthenticationToken;
import com.enonic.wem.api.security.auth.EmailPasswordAuthToken;
import com.enonic.wem.api.security.auth.UsernamePasswordAuthToken;

import static java.util.stream.Collectors.toList;

public final class SecurityServiceImpl
    implements SecurityService
{

    private final List<UserStore> userStores;

    private final ConcurrentMap<PrincipalKey, Principal> principals;


    public SecurityServiceImpl()
    {
        this.userStores = new CopyOnWriteArrayList<>();
        this.principals = new ConcurrentHashMap<>();

        final UserStore systemUserStore = UserStore.newUserStore().key( UserStoreKey.system() ).displayName( "System" ).build();
        this.userStores.add( systemUserStore );
    }

    @Override
    public UserStores getUserStores()
    {
        return UserStores.from( this.userStores );
    }

    @Override
    public Principals getPrincipals( final UserStoreKey userStore, final PrincipalType type )
    {
        final List<Principal> principals = this.principals.values().stream().
            filter( principal -> principal.getKey().getUserStore().equals( userStore ) ).
            filter( principal -> principal.getKey().getType() == type ).
            collect( toList() );

        return Principals.from( principals );
    }

    @Override
    public AuthenticationInfo authenticate( final AuthenticationToken token )
    {
        if ( token instanceof UsernamePasswordAuthToken )
        {
            final UsernamePasswordAuthToken authToken = (UsernamePasswordAuthToken) token;
            final User user = findByUsername( authToken.getUserStore(), authToken.getUsername() );
            return user != null ? AuthenticationInfo.newBuilder().user( user ).build() : null;
        }
        if ( token instanceof EmailPasswordAuthToken )
        {
            final EmailPasswordAuthToken authToken = (EmailPasswordAuthToken) token;
            final User user = findByEmail( authToken.getUserStore(), authToken.getEmail() );
            return user != null ? AuthenticationInfo.newBuilder().user( user ).build() : null;
        }
        else
        {
            throw new UnsupportedOperationException( "Authentication token not supported: " + token.getClass().getName() );
        }
    }

    private User findByUsername( final UserStoreKey userStore, final String username )
    {
        return (User) this.principals.values().stream().
            filter( principal -> principal.getKey().getUserStore().equals( userStore ) ).
            filter( principal -> principal.getKey().isUser() ).
            filter( principal -> username.equals( ( (User) principal ).getLogin() ) ).
            findFirst().orElse( null );
    }

    private User findByEmail( final UserStoreKey userStore, final String email )
    {
        return (User) this.principals.values().stream().
            filter( principal -> principal.getKey().getUserStore().equals( userStore ) ).
            filter( principal -> principal.getKey().isUser() ).
            filter( principal -> email.equals( ( (User) principal ).getEmail() ) ).
            findFirst().orElse( null );
    }

    @Override
    public void setPassword( final PrincipalKey key, final String password )
    {

    }

    @Override
    public void createUser( final User user )
    {
        if ( this.principals.putIfAbsent( user.getKey(), user ) != null )
        {
            throw new IllegalArgumentException( "User already exists: " + user.getKey() );
        }
    }

    @Override
    public void updateUser( final User user )
    {
        if ( this.principals.replace( user.getKey(), user ) == null )
        {
            throw new IllegalArgumentException( "Could not find user to be updated: " + user.getKey() );
        }
    }

    @Override
    public User getUser( final PrincipalKey userKey )
    {
        Preconditions.checkArgument( userKey.isUser(), "Expected principal key of type User" );
        return (User) this.principals.get( userKey );
    }

    @Override
    public void createGroup( final Group group )
    {
        if ( this.principals.putIfAbsent( group.getKey(), group ) != null )
        {
            throw new IllegalArgumentException( "Group already exists: " + group.getKey() );
        }
    }

    @Override
    public void updateGroup( final Group group )
    {
        if ( this.principals.replace( group.getKey(), group ) == null )
        {
            throw new IllegalArgumentException( "Could not find group to be updated: " + group.getKey() );
        }
    }

    @Override
    public Group getGroup( final PrincipalKey groupKey )
    {
        Preconditions.checkArgument( groupKey.isGroup(), "Expected principal key of type Group" );
        return (Group) this.principals.get( groupKey );
    }

    @Override
    public PrincipalQueryResult query( final PrincipalQuery query )
    {
        final Predicate<Principal> userStorePredicate = principal -> query.getUserStores().contains( principal.getKey().getUserStore() );
        final Predicate<Principal> typePredicate = principal -> query.getPrincipalTypes().contains( principal.getKey().getType() );

        final long total = this.principals.values().stream().
            filter( userStorePredicate ).
            filter( typePredicate ).
            count();

        final List<Principal> principals = this.principals.values().stream().
            filter( userStorePredicate ).
            filter( typePredicate ).
            limit( query.getSize() ).
            skip( query.getFrom() ).
            collect( toList() );

        return PrincipalQueryResult.newResult().
            totalSize( Ints.checkedCast( total ) ).
            addPrincipals( principals ).
            build();
    }
}
