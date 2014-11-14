package com.enonic.wem.core.security;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.query.expr.CompareExpr;
import com.enonic.wem.api.query.expr.FieldExpr;
import com.enonic.wem.api.query.expr.LogicalExpr;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.expr.ValueExpr;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.api.security.CreateGroupParams;
import com.enonic.wem.api.security.CreateRoleParams;
import com.enonic.wem.api.security.CreateUserParams;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
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
import com.enonic.wem.repo.CreateNodeParams;
import com.enonic.wem.repo.FindNodesByQueryResult;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodeNotFoundException;
import com.enonic.wem.repo.NodeQuery;
import com.enonic.wem.repo.NodeService;
import com.enonic.wem.repo.UpdateNodeParams;

import static com.enonic.wem.api.security.SystemConstants.CONTEXT_USER_STORES;
import static com.enonic.wem.core.security.PrincipalKeyNodeTranslator.toNodeId;
import static com.enonic.wem.core.security.PrincipalNodeTranslator.EMAIL_KEY;
import static com.enonic.wem.core.security.PrincipalNodeTranslator.LOGIN_KEY;
import static com.enonic.wem.core.security.PrincipalNodeTranslator.USER_STORE_KEY;

public final class SecurityServiceImpl
    implements SecurityService
{
    private final List<UserStore> userStores;

    private NodeService nodeService;

    private Clock clock;

    public SecurityServiceImpl()
    {
        this.clock = Clock.systemUTC();
        this.userStores = new CopyOnWriteArrayList<>();

        final UserStore systemUserStore = UserStore.newUserStore().key( UserStoreKey.system() ).displayName( "System" ).build();
        this.userStores.add( systemUserStore );
    }

    @Override
    public PrincipalRelationships getRelationships( final PrincipalKey from )
    {
        try
        {
            final Node node = CONTEXT_USER_STORES.callWith( () -> this.nodeService.getById( toNodeId( from ) ) );
            return PrincipalNodeTranslator.relationshipsFromNode( node );
        }
        catch ( NodeNotFoundException e )
        {
            return PrincipalRelationships.empty();
        }
    }

    @Override
    public void addRelationship( final PrincipalRelationship relationship )
    {
        CONTEXT_USER_STORES.callWith( () -> {
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.addRelationshipToUpdateNodeParams( relationship );
            nodeService.update( updateNodeParams );
            return null;
        } );
    }

    @Override
    public void removeRelationship( final PrincipalRelationship relationship )
    {
        CONTEXT_USER_STORES.callWith( () -> {
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.removeRelationshipToUpdateNodeParams( relationship );
            nodeService.update( updateNodeParams );
            return null;
        } );
    }

    @Override
    public void removeRelationships( final PrincipalKey from )
    {
        CONTEXT_USER_STORES.callWith( () -> {
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.removeAllRelationshipsToUpdateNodeParams( from );
            nodeService.update( updateNodeParams );
            return null;
        } );
    }

    private PrincipalKeys resolveMemberships( final PrincipalKey userKey )
    {
        final Set<PrincipalKey> resolvedMemberships = Sets.newHashSet();
        final PrincipalKeys directMemberships = queryMemberships( userKey );
        resolvedMemberships.addAll( directMemberships.getSet() );

        final Set<PrincipalKey> queriedMemberships = Sets.newHashSet();

        do
        {
            final Set<PrincipalKey> newMemberships = Sets.newHashSet();
            for ( PrincipalKey principal : resolvedMemberships )
            {
                if ( !queriedMemberships.contains( principal ) )
                {
                    final PrincipalKeys indirectMemberships = queryMemberships( principal );
                    newMemberships.addAll( indirectMemberships.getSet() );
                    queriedMemberships.add( principal );
                }
            }
            resolvedMemberships.addAll( newMemberships );
        }
        while ( resolvedMemberships.size() > queriedMemberships.size() );

        return PrincipalKeys.from( resolvedMemberships );
    }

    private PrincipalKeys queryMemberships( final PrincipalKey member )
    {
        try
        {
            final FindNodesByQueryResult result = CONTEXT_USER_STORES.callWith( () -> this.nodeService.findByQuery( NodeQuery.create().
                addQueryFilter( ValueFilter.create().
                    fieldName( PrincipalNodeTranslator.MEMBER_KEY ).
                    addValue( Value.newString( member.toString() ) ).
                    build() ).
                build() ) );

            return PrincipalKeyNodeTranslator.fromNodes( result.getNodes() );
        }
        catch ( NodeNotFoundException e )
        {
            return PrincipalKeys.empty();
        }
    }

    @Override
    public UserStores getUserStores()
    {
        return UserStores.from( this.userStores );
    }

    @Override
    public Principals getPrincipals( final UserStoreKey userStore, final PrincipalType type )
    {
        try
        {
            final FindNodesByQueryResult result = CONTEXT_USER_STORES.callWith( () -> this.nodeService.findByQuery( NodeQuery.create().
                addQueryFilter( ValueFilter.create().
                    fieldName( USER_STORE_KEY ).
                    addValue( Value.newString( userStore.toString() ) ).
                    build() ).
                addQueryFilter( ValueFilter.create().
                    fieldName( PrincipalNodeTranslator.PRINCIPAL_TYPE_KEY ).
                    addValue( Value.newString( type.toString() ) ).
                    build() ).
                build() ) );

            return PrincipalNodeTranslator.fromNodes( result.getNodes() );
        }
        catch ( NodeNotFoundException e )
        {
            return Principals.empty();
        }
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
            final PrincipalKeys principals = resolveMemberships( user.getKey() );
            return AuthenticationInfo.create().principals( principals ).user( user ).build();
        }
        else
        {
            return AuthenticationInfo.failed();
        }
    }

    private AuthenticationInfo authenticateUsernamePassword( final UsernamePasswordAuthToken token )
    {
        final User user = findByUsername( token.getUserStore(), token.getUsername() );
        if ( user != null && !user.isDisabled() && passwordMatch( user, token.getPassword() ) )
        {
            final PrincipalKeys principals = resolveMemberships( user.getKey() );
            return AuthenticationInfo.create().user( user ).principals( principals ).build();
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

    private User findByUsername( final UserStoreKey userStore, final String username )
    {
        final CompareExpr userStoreExpr =
            CompareExpr.create( FieldExpr.from( USER_STORE_KEY ), CompareExpr.Operator.EQ, ValueExpr.string( userStore.toString() ) );
        final CompareExpr userNameExpr =
            CompareExpr.create( FieldExpr.from( LOGIN_KEY ), CompareExpr.Operator.EQ, ValueExpr.string( username ) );
        final QueryExpr query = QueryExpr.from( LogicalExpr.and( userStoreExpr, userNameExpr ) );
        final FindNodesByQueryResult result =
            CONTEXT_USER_STORES.callWith( () -> nodeService.findByQuery( NodeQuery.create().query( query ).build() ) );

        if ( result.getNodes().getSize() > 1 )
        {
            throw new IllegalArgumentException( "Expected at most 1 user with username " + username + " in userstore " + userStore );
        }

        return result.getNodes().isEmpty() ? null : PrincipalNodeTranslator.userFromNode( result.getNodes().first() );
    }

    private User findByEmail( final UserStoreKey userStore, final String email )
    {
        final CompareExpr userStoreExpr =
            CompareExpr.create( FieldExpr.from( USER_STORE_KEY ), CompareExpr.Operator.EQ, ValueExpr.string( userStore.toString() ) );
        final CompareExpr userNameExpr =
            CompareExpr.create( FieldExpr.from( EMAIL_KEY ), CompareExpr.Operator.EQ, ValueExpr.string( email ) );
        final QueryExpr query = QueryExpr.from( LogicalExpr.and( userStoreExpr, userNameExpr ) );
        final FindNodesByQueryResult result =
            CONTEXT_USER_STORES.callWith( () -> nodeService.findByQuery( NodeQuery.create().query( query ).build() ) );

        if ( result.getNodes().getSize() > 1 )
        {
            throw new IllegalArgumentException( "Expected at most 1 user with email " + email + " in userstore " + userStore );
        }

        return result.getNodes().isEmpty() ? null : PrincipalNodeTranslator.userFromNode( result.getNodes().first() );
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
            modifiedTime( Instant.now( clock ) ).
            build();

        if ( createUser.getPassword() != null )
        {
            setPassword( user.getKey(), createUser.getPassword() );
        }

        final CreateNodeParams createNodeParams = PrincipalNodeTranslator.toCreateNodeParams( user );
        final Node node = CONTEXT_USER_STORES.callWith( () -> nodeService.create( createNodeParams ) );

        return PrincipalNodeTranslator.userFromNode( node );
    }

    @Override
    public User updateUser( final UpdateUserParams updateUserParams )
    {
        return CONTEXT_USER_STORES.callWith( () -> {

            final Node node;
            try
            {
                node = this.nodeService.getById( toNodeId( updateUserParams.getKey() ) );
            }
            catch ( NodeNotFoundException e )
            {
                throw new IllegalArgumentException( "User to be updated not found: " + updateUserParams.getKey() );
            }

            final User existingUser = PrincipalNodeTranslator.userFromNode( node );

            final User userToUpdate = updateUserParams.update( existingUser );
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.toUpdateNodeParams( userToUpdate );

            final Node updatedNode = nodeService.update( updateNodeParams );
            return PrincipalNodeTranslator.userFromNode( updatedNode );
        } );
    }

    @Override
    public Optional<User> getUser( final PrincipalKey userKey )
    {
        Preconditions.checkArgument( userKey.isUser(), "Expected principal key of type User" );

        try
        {
            final Node node = CONTEXT_USER_STORES.callWith( () -> this.nodeService.getById( toNodeId( userKey ) ) );
            return Optional.ofNullable( PrincipalNodeTranslator.userFromNode( node ) );
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
            modifiedTime( Instant.now( clock ) ).
            build();

        final CreateNodeParams createGroupParams = PrincipalNodeTranslator.toCreateNodeParams( group );
        final Node node = CONTEXT_USER_STORES.callWith( () -> this.nodeService.create( createGroupParams ) );

        return PrincipalNodeTranslator.groupFromNode( node );
    }

    @Override
    public Group updateGroup( final UpdateGroupParams updateGroupParams )
    {
        return CONTEXT_USER_STORES.callWith( () -> {

            final Node node;
            try
            {
                node = this.nodeService.getById( toNodeId( updateGroupParams.getKey() ) );
            }
            catch ( NodeNotFoundException e )
            {
                throw new IllegalArgumentException( "Group to be updated not found: " + updateGroupParams.getKey() );
            }

            final Group existingGroup = PrincipalNodeTranslator.groupFromNode( node );

            final Group groupToUpdate = updateGroupParams.update( existingGroup );
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.toUpdateNodeParams( groupToUpdate );

            final Node updatedNode = nodeService.update( updateNodeParams );
            return PrincipalNodeTranslator.groupFromNode( updatedNode );
        } );
    }

    @Override
    public Optional<Group> getGroup( final PrincipalKey groupKey )
    {
        Preconditions.checkArgument( groupKey.isGroup(), "Expected principal key of type Group" );

        try
        {
            final Node node = CONTEXT_USER_STORES.callWith( () -> this.nodeService.getById( toNodeId( groupKey ) ) );
            return Optional.ofNullable( PrincipalNodeTranslator.groupFromNode( node ) );
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
            modifiedTime( Instant.now( clock ) ).
            build();

        final CreateNodeParams createNodeParams = PrincipalNodeTranslator.toCreateNodeParams( role );
        final Node node = CONTEXT_USER_STORES.callWith( () -> this.nodeService.create( createNodeParams ) );

        return PrincipalNodeTranslator.roleFromNode( node );
    }

    @Override
    public Role updateRole( final UpdateRoleParams updateRoleParams )
    {
        return CONTEXT_USER_STORES.callWith( () -> {

            final Node node;
            try
            {
                node = this.nodeService.getById( toNodeId( updateRoleParams.getKey() ) );
            }
            catch ( NodeNotFoundException e )
            {
                throw new IllegalArgumentException( "Role to be updated not found: " + updateRoleParams.getKey() );
            }

            final Role existingRole = PrincipalNodeTranslator.roleFromNode( node );

            final Role roleToUpdate = updateRoleParams.update( existingRole );
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.toUpdateNodeParams( roleToUpdate );

            final Node updatedNode = nodeService.update( updateNodeParams );
            return PrincipalNodeTranslator.roleFromNode( updatedNode );
        } );
    }

    @Override
    public Optional<Role> getRole( final PrincipalKey roleKey )
    {
        Preconditions.checkArgument( roleKey.isRole(), "Expected principal key of type Role" );

        try
        {
            final Node node = CONTEXT_USER_STORES.callWith( () -> this.nodeService.getById( toNodeId( roleKey ) ) );
            return Optional.ofNullable( PrincipalNodeTranslator.roleFromNode( node ) );
        }
        catch ( Exception e )
        {
            return Optional.empty();
        }
    }

    @Override
    public Optional<? extends Principal> getPrincipal( final PrincipalKey principalKey )
    {
        switch ( Objects.requireNonNull( principalKey, "Principal key was null" ).getType() )
        {
            case USER:
                return getUser( principalKey );

            case GROUP:
                return getGroup( principalKey );

            case ROLE:
                return getRole( principalKey );
        }
        return Optional.empty();
    }

    @Override
    public PrincipalQueryResult query( final PrincipalQuery query )
    {
        // TODO: FIX

        /*
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
        */

        return null;

    }

    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    public void setClock( final Clock clock )
    {
        this.clock = clock;
    }
}
