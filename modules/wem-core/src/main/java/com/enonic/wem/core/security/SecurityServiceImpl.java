package com.enonic.wem.core.security;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.primitives.Ints;

import com.enonic.wem.api.security.CreateGroupParams;
import com.enonic.wem.api.security.CreateRoleParams;
import com.enonic.wem.api.security.CreateUserParams;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalQuery;
import com.enonic.wem.api.security.PrincipalQueryResult;
import com.enonic.wem.api.security.PrincipalRelationship;
import com.enonic.wem.api.security.PrincipalRelationships;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.Role;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.UpdateGroupParams;
import com.enonic.wem.api.security.UpdateRoleParams;
import com.enonic.wem.api.security.UpdateUserParams;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStores;
import com.enonic.wem.api.security.auth.AuthenticationException;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.security.auth.AuthenticationToken;
import com.enonic.wem.api.security.auth.EmailPasswordAuthToken;
import com.enonic.wem.api.security.auth.UsernamePasswordAuthToken;
import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodeService;

import static java.util.stream.Collectors.toList;

public final class SecurityServiceImpl
    implements SecurityService
{

    private final List<UserStore> userStores;

    private final ConcurrentMap<PrincipalKey, Principal> principals;

    private final Multimap<PrincipalKey, PrincipalRelationship> relationshipTable;

    private NodeService nodeService;

    public SecurityServiceImpl()
    {
        this.userStores = new CopyOnWriteArrayList<>();
        this.principals = new ConcurrentHashMap<>();
        this.relationshipTable = Multimaps.synchronizedSetMultimap( HashMultimap.create() );

        final UserStore systemUserStore = UserStore.newUserStore().key( UserStoreKey.system() ).displayName( "System" ).build();
        this.userStores.add( systemUserStore );
    }

    @Override
    public PrincipalRelationships getRelationships( final PrincipalKey from )
    {
        final Collection<PrincipalRelationship> relationships = this.relationshipTable.get( from );
        if ( relationships == null )
        {
            return PrincipalRelationships.empty();
        }

        final PrincipalRelationships principalRelationships;
        synchronized ( this.relationshipTable )
        {
            principalRelationships = PrincipalRelationships.from( relationships );
        }
        return principalRelationships;
    }

    @Override
    public void addRelationship( final PrincipalRelationship relationship )
    {
        this.relationshipTable.put( relationship.getFrom(), relationship );
    }

    @Override
    public void removeRelationship( final PrincipalRelationship relationship )
    {
        this.relationshipTable.remove( relationship.getFrom(), relationship );
    }

    @Override
    public void removeRelationships( final PrincipalKey from )
    {
        this.relationshipTable.removeAll( from );
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
            return authenticateUsernamePassword( (UsernamePasswordAuthToken) token );
        }
        else if ( token instanceof EmailPasswordAuthToken )
        {
            return authenticateEmailPassword( (EmailPasswordAuthToken) token );
        }
        else
        {
            throw new AuthenticationException( "Authentication token not supported: " + token.getClass().getSimpleName() );
        }
    }

    private AuthenticationInfo authenticateEmailPassword( final EmailPasswordAuthToken token )
    {
        final User user = findByEmail( token.getUserStore(), token.getEmail() );
        if ( user != null && !user.isDisabled() && passwordMatch( user, token.getPassword() ) )
        {
            return AuthenticationInfo.create().user( user ).build();
        }
        else
        {
            return AuthenticationInfo.failed();
        }
    }

    private AuthenticationInfo authenticateUsernamePassword( final UsernamePasswordAuthToken token )
    {
        final PrincipalKey userKey = PrincipalKey.ofUser( token.getUserStore(), token.getUsername() );
        final User user = getUser( userKey ).orElse( null );
        if ( user != null && !user.isDisabled() && passwordMatch( user, token.getPassword() ) )
        {
            return AuthenticationInfo.create().user( user ).build();
        }
        else
        {
            return AuthenticationInfo.failed();
        }
    }

    private boolean passwordMatch( final User user, final String password )
    {
        return "password".equals( password );
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
    public User createUser( final CreateUserParams createUser )
    {
        final User user = User.create().
            key( createUser.getKey() ).
            login( createUser.getLogin() ).
            email( createUser.getEmail() ).
            displayName( createUser.getDisplayName() ).
            build();

        if ( this.principals.putIfAbsent( user.getKey(), user ) != null )
        {
            throw new IllegalArgumentException( "User already exists: " + user.getKey() );
        }

        if ( createUser.getPassword() != null )
        {
            setPassword( user.getKey(), createUser.getPassword() );
        }

        final CreateNodeParams createNodeParams = UserNodeTranslator.toCreateNodeParams( user );
        final Node node = nodeService.create( createNodeParams );

        return UserNodeTranslator.fromNode( node );
    }

    @Override
    public User updateUser( final UpdateUserParams updateUser )
    {
        final User updatedUser = (User) this.principals.computeIfPresent( updateUser.getKey(), ( userKey, principal ) -> {
            final User existingUser = (User) principal;
            return updateUser.update( existingUser );
        } );

        if ( updatedUser == null )
        {
            throw new IllegalArgumentException( "Could not find user to be updated: " + updateUser.getKey() );
        }
        return updatedUser;
    }

    @Override
    public Optional<User> getUser( final PrincipalKey userKey )
    {
        Preconditions.checkArgument( userKey.isUser(), "Expected principal key of type User" );

        try
        {
            final Node node = this.nodeService.getById( NodeId.from( userKey.toString() ) );
            return Optional.ofNullable( UserNodeTranslator.fromNode( node ) );
        }
        catch ( Exception e )
        {
            return Optional.empty();
        }
    }

    @Override
    public Group createGroup( final CreateGroupParams createGroup )
    {
        final Group group = Group.create().
            key( createGroup.getKey() ).
            displayName( createGroup.getDisplayName() ).
            build();
        if ( this.principals.putIfAbsent( group.getKey(), group ) != null )
        {
            throw new IllegalArgumentException( "Group already exists: " + group.getKey() );
        }

        final CreateNodeParams createGroupParams = GroupNodeTranslator.toCreateNodeParams( group );
        final Node node = this.nodeService.create( createGroupParams );

        return GroupNodeTranslator.fromNode( node );
    }

    @Override
    public Group updateGroup( final UpdateGroupParams updateGroup )
    {
        final Group updatedGroup = (Group) this.principals.computeIfPresent( updateGroup.getKey(), ( userKey, principal ) -> {
            final Group existingGroup = (Group) principal;
            return updateGroup.update( existingGroup );
        } );

        if ( updatedGroup == null )
        {
            throw new IllegalArgumentException( "Could not find group to be updated: " + updateGroup.getKey() );
        }
        return updatedGroup;
    }

    @Override
    public Optional<Group> getGroup( final PrincipalKey groupKey )
    {
        Preconditions.checkArgument( groupKey.isGroup(), "Expected principal key of type Group" );

        try
        {
            final Node node = this.nodeService.getById( NodeId.from( groupKey.toString() ) );
            return Optional.ofNullable( GroupNodeTranslator.fromNode( node ) );
        }
        catch ( Exception e )
        {
            return Optional.empty();
        }
    }

    @Override
    public Role createRole( final CreateRoleParams createRole )
    {
        final Role role = Role.create().
            key( createRole.getKey() ).
            displayName( createRole.getDisplayName() ).
            build();
        if ( this.principals.putIfAbsent( role.getKey(), role ) != null )
        {
            throw new IllegalArgumentException( "Role already exists: " + role.getKey() );
        }

        final CreateNodeParams createNodeParams = RoleNodeTranslator.toCreateNodeParams( role );
        final Node node = this.nodeService.create( createNodeParams );

        return RoleNodeTranslator.fromNode( node );
    }

    @Override
    public Role updateRole( final UpdateRoleParams updateRole )
    {
        final Role updatedRole = (Role) this.principals.computeIfPresent( updateRole.getKey(), ( userKey, principal ) -> {
            final Role existingRole = (Role) principal;
            return updateRole.update( existingRole );
        } );

        if ( updatedRole == null )
        {
            throw new IllegalArgumentException( "Could not find role to be updated: " + updateRole.getKey() );
        }
        return updatedRole;
    }

    @Override
    public Optional<Role> getRole( final PrincipalKey roleKey )
    {
        Preconditions.checkArgument( roleKey.isRole(), "Expected principal key of type Role" );

        try
        {
            final Node node = this.nodeService.getById( NodeId.from( roleKey.toString() ) );
            return Optional.ofNullable( RoleNodeTranslator.fromNode( node ) );
        }
        catch ( Exception e )
        {
            return Optional.empty();
        }
    }

    @Override
    public PrincipalQueryResult query( final PrincipalQuery query )
    {
        final Predicate<Principal> userStoreFilter = principal -> query.getUserStores().contains( principal.getKey().getUserStore() );
        final Predicate<Principal> typeFilter = principal -> query.getPrincipalTypes().contains( principal.getKey().getType() );
        final Predicate<Principal> keysFilter = principal -> query.getPrincipals().contains( principal.getKey() );

        Stream<Principal> streamCount = this.principals.values().stream();
        if ( query.getUserStores().isNotEmpty() )
        {
            streamCount = streamCount.filter( userStoreFilter );
        }
        if ( query.getPrincipals().isNotEmpty() )
        {
            streamCount = streamCount.filter( keysFilter );
        }
        final long total = streamCount.
            filter( typeFilter ).
            count();

        Stream<Principal> streamPrincipals = this.principals.values().stream();
        if ( query.getUserStores().isNotEmpty() )
        {
            streamPrincipals = streamPrincipals.filter( userStoreFilter );
        }
        if ( query.getPrincipals().isNotEmpty() )
        {
            streamPrincipals = streamPrincipals.filter( keysFilter );
        }
        final List<Principal> principals = streamPrincipals.
            filter( typeFilter ).
            limit( query.getSize() ).
            skip( query.getFrom() ).
            collect( toList() );

        return PrincipalQueryResult.newResult().
            totalSize( Ints.checkedCast( total ) ).
            addPrincipals( principals ).
            build();
    }

    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
