package com.enonic.xp.core.impl.security;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.node.ApplyNodePermissionsParams;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.FindNodesByQueryResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeAlreadyExistAtPathException;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeNotFoundException;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.node.RootNode;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.query.expr.CompareExpr;
import com.enonic.wem.api.query.expr.FieldExpr;
import com.enonic.wem.api.query.expr.LogicalExpr;
import com.enonic.wem.api.query.expr.QueryExpr;
import com.enonic.wem.api.query.expr.ValueExpr;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.api.security.CreateGroupParams;
import com.enonic.wem.api.security.CreateRoleParams;
import com.enonic.wem.api.security.CreateUserParams;
import com.enonic.wem.api.security.CreateUserStoreParams;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.PrincipalAlreadyExistsException;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.PrincipalNotFoundException;
import com.enonic.wem.api.security.PrincipalQuery;
import com.enonic.wem.api.security.PrincipalQueryResult;
import com.enonic.wem.api.security.PrincipalRelationship;
import com.enonic.wem.api.security.PrincipalRelationships;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.Role;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.UpdateGroupParams;
import com.enonic.wem.api.security.UpdateRoleParams;
import com.enonic.wem.api.security.UpdateUserParams;
import com.enonic.wem.api.security.UpdateUserStoreParams;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStores;
import com.enonic.wem.api.security.acl.AccessControlList;
import com.enonic.wem.api.security.acl.UserStoreAccessControlList;
import com.enonic.wem.api.security.auth.AuthenticationException;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.security.auth.AuthenticationToken;
import com.enonic.wem.api.security.auth.EmailPasswordAuthToken;
import com.enonic.wem.api.security.auth.UsernamePasswordAuthToken;

import static com.enonic.wem.api.security.SystemConstants.CONTEXT_SECURITY;
import static com.enonic.xp.core.impl.security.PrincipalKeyNodeTranslator.toNodeId;

@Component(immediate = true)
public final class SecurityServiceImpl
    implements SecurityService
{
    private static final ImmutableSet<PrincipalKey> FORBIDDEN_FROM_RELATIONSHIP = ImmutableSet.of( RoleKeys.EVERYONE );

    private NodeService nodeService;

    private Clock clock;

    public SecurityServiceImpl()
    {
        this.clock = Clock.systemUTC();
    }

    private final PasswordEncoder passwordEncoder = new PBKDF2Encoder();

    @Activate
    public void initialize()
    {
        new SecurityInitializer( this, this.nodeService ).initialize();
    }

    @Override
    public UserStores getUserStores()
    {
        final FindNodesByParentParams findByParent = FindNodesByParentParams.create().
            parentPath( UserStoreNodeTranslator.getUserStoresParentPath() ).build();
        final FindNodesByParentResult result = callWithContext( () -> this.nodeService.findByParent( findByParent ) );

        return UserStoreNodeTranslator.fromNodes( result.getNodes() );
    }

    @Override
    public UserStore getUserStore( final UserStoreKey userStore )
    {
        final NodePath userStoreNodePath = UserStoreNodeTranslator.toUserStoreNodePath( userStore );
        final Node node = callWithContext( () -> this.nodeService.getByPath( userStoreNodePath ) );
        return node == null ? null : UserStoreNodeTranslator.fromNode( node );
    }

    @Override
    public UserStoreAccessControlList getUserStorePermissions( final UserStoreKey userStore )
    {
        final NodePath userStoreNodePath = UserStoreNodeTranslator.toUserStoreNodePath( userStore );
        final NodePath usersNodePath = UserStoreNodeTranslator.toUserStoreUsersNodePath( userStore );
        final NodePath groupsNodePath = UserStoreNodeTranslator.toUserStoreGroupsNodePath( userStore );

        final Node userStoreNode = callWithContext( () -> this.nodeService.getByPath( userStoreNodePath ) );
        final Node usersNode = callWithContext( () -> this.nodeService.getByPath( usersNodePath ) );
        final Node groupsNode = callWithContext( () -> this.nodeService.getByPath( groupsNodePath ) );

        return UserStoreNodeTranslator.userStorePermissionsFromNode( userStoreNode, usersNode, groupsNode );
    }

    @Override
    public PrincipalRelationships getRelationships( final PrincipalKey from )
    {
        try
        {
            final Node node = callWithContext( () -> this.nodeService.getById( toNodeId( from ) ) );
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
        final PrincipalKey from = relationship.getFrom();
        if ( FORBIDDEN_FROM_RELATIONSHIP.contains( from ) )
        {
            throw new IllegalArgumentException( "Invalid 'from' value in relationship: " + from.toString() );
        }
        callWithContext( () -> {
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.addRelationshipToUpdateNodeParams( relationship );
            nodeService.update( updateNodeParams );
            return null;
        } );
    }

    @Override
    public void removeRelationship( final PrincipalRelationship relationship )
    {
        callWithContext( () -> {
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.removeRelationshipToUpdateNodeParams( relationship );
            nodeService.update( updateNodeParams );
            return null;
        } );
    }

    @Override
    public void removeRelationships( final PrincipalKey from )
    {
        callWithContext( () -> {
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.removeAllRelationshipsToUpdateNodeParams( from );
            nodeService.update( updateNodeParams );
            return null;
        } );
    }

    private PrincipalKeys resolveMemberships( final PrincipalKey userKey )
    {
        final Set<PrincipalKey> resolvedMemberships = Sets.newHashSet();
        final PrincipalKeys directMemberships = queryDirectMemberships( userKey );
        resolvedMemberships.addAll( directMemberships.getSet() );

        final Set<PrincipalKey> queriedMemberships = Sets.newHashSet();

        do
        {
            final Set<PrincipalKey> newMemberships = Sets.newHashSet();
            for ( PrincipalKey principal : resolvedMemberships )
            {
                if ( !queriedMemberships.contains( principal ) )
                {
                    final PrincipalKeys indirectMemberships = queryDirectMemberships( principal );
                    newMemberships.addAll( indirectMemberships.getSet() );
                    queriedMemberships.add( principal );
                }
            }
            resolvedMemberships.addAll( newMemberships );
        }
        while ( resolvedMemberships.size() > queriedMemberships.size() );

        return PrincipalKeys.from( resolvedMemberships );
    }

    private PrincipalKeys queryDirectMemberships( final PrincipalKey member )
    {
        try
        {
            final FindNodesByQueryResult result = callWithContext( () -> this.nodeService.findByQuery( NodeQuery.create().
                addQueryFilter( ValueFilter.create().
                    fieldName( PrincipalPropertyNames.MEMBER_KEY ).
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
    public Principals findPrincipals( final UserStoreKey userStore, final List<PrincipalType> types, final String query )
    {
        final PrincipalQuery.Builder principalQuery = PrincipalQuery.newQuery().
            getAll().
            includeTypes( types ).
            searchText( query );
        if ( userStore != null )
        {
            principalQuery.userStore( userStore );
        }

        final PrincipalQueryResult result = query( principalQuery.build() );
        return result.getPrincipals();
    }

    @Override
    public PrincipalKeys getMemberships( final PrincipalKey userKey )
    {
        return queryDirectMemberships( userKey );
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
            return createAuthInfo( user );
        }
        else
        {
            return AuthenticationInfo.unAuthenticated();
        }
    }

    private AuthenticationInfo authenticateUsernamePassword( final UsernamePasswordAuthToken token )
    {
        final User user = findByUsername( token.getUserStore(), token.getUsername() );
        if ( user != null && !user.isDisabled() && passwordMatch( user, token.getPassword() ) )
        {
            return createAuthInfo( user );
        }
        else
        {
            return AuthenticationInfo.unAuthenticated();
        }
    }

    private AuthenticationInfo createAuthInfo( final User user )
    {
        final PrincipalKeys principals = resolveMemberships( user.getKey() );
        return AuthenticationInfo.create().principals( principals ).
            principals( RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE ).
            user( user ).build();
    }

    private boolean passwordMatch( final User user, final String password )
    {
        if ( Strings.isNullOrEmpty( password ) )
        {
            return false;
        }

        return this.passwordEncoder.validate( password, user.getAuthenticationHash() );
    }

    private User findByUsername( final UserStoreKey userStore, final String username )
    {
        final CompareExpr userStoreExpr = CompareExpr.create( FieldExpr.from( PrincipalIndexPath.USER_STORE_KEY ), CompareExpr.Operator.EQ,
                                                              ValueExpr.string( userStore.toString() ) );
        final CompareExpr userNameExpr =
            CompareExpr.create( FieldExpr.from( PrincipalIndexPath.LOGIN_KEY ), CompareExpr.Operator.EQ, ValueExpr.string( username ) );
        final QueryExpr query = QueryExpr.from( LogicalExpr.and( userStoreExpr, userNameExpr ) );
        final FindNodesByQueryResult result = callWithContext( () -> nodeService.findByQuery( NodeQuery.create().query( query ).build() ) );

        if ( result.getNodes().getSize() > 1 )
        {
            throw new IllegalArgumentException( "Expected at most 1 user with username " + username + " in userstore " + userStore );
        }

        return result.getNodes().isEmpty() ? null : PrincipalNodeTranslator.userFromNode( result.getNodes().first() );
    }

    private User findByEmail( final UserStoreKey userStore, final String email )
    {
        final CompareExpr userStoreExpr = CompareExpr.create( FieldExpr.from( PrincipalIndexPath.USER_STORE_KEY ), CompareExpr.Operator.EQ,
                                                              ValueExpr.string( userStore.toString() ) );
        final CompareExpr userNameExpr =
            CompareExpr.create( FieldExpr.from( PrincipalIndexPath.EMAIL_KEY ), CompareExpr.Operator.EQ, ValueExpr.string( email ) );
        final QueryExpr query = QueryExpr.from( LogicalExpr.and( userStoreExpr, userNameExpr ) );
        final FindNodesByQueryResult result = callWithContext( () -> nodeService.findByQuery( NodeQuery.create().query( query ).build() ) );

        if ( result.getNodes().getSize() > 1 )
        {
            throw new IllegalArgumentException( "Expected at most 1 user with email " + email + " in userstore " + userStore );
        }

        return result.getNodes().isEmpty() ? null : PrincipalNodeTranslator.userFromNode( result.getNodes().first() );
    }

    @Override
    public User setPassword( final PrincipalKey key, final String password )
    {
        Preconditions.checkArgument( key.isUser(), "Expected principal key of type User" );

        return callWithContext( () -> {

            final Node node = callWithContext( () -> this.nodeService.getById( toNodeId( key ) ) );

            final User user = PrincipalNodeTranslator.userFromNode( node );

            if ( user == null )
            {
                throw new NodeNotFoundException( "setPassword failed, user with key " + key + " not found" );
            }

            final String authenticationHash = this.passwordEncoder.encodePassword( password );

            final User userToUpdate = User.create( user ).
                authenticationHash( authenticationHash ).
                build();

            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.toUpdateNodeParams( userToUpdate );

            final Node updatedNode = nodeService.update( updateNodeParams );
            return PrincipalNodeTranslator.userFromNode( updatedNode );
        } );
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

        final CreateNodeParams createNodeParams = PrincipalNodeTranslator.toCreateNodeParams( user );
        try
        {
            final Node node = callWithContext( () -> nodeService.create( createNodeParams ) );
            if ( createUser.getPassword() != null )
            {
                return setPassword( user.getKey(), createUser.getPassword() );
            }

            return PrincipalNodeTranslator.userFromNode( node );
        }
        catch ( NodeAlreadyExistAtPathException e )
        {
            throw new PrincipalAlreadyExistsException( createUser.getKey() );
        }
    }

    @Override
    public User updateUser( final UpdateUserParams updateUserParams )
    {
        return callWithContext( () -> {

            final Node node;
            try
            {
                node = this.nodeService.getById( toNodeId( updateUserParams.getKey() ) );
            }
            catch ( NodeNotFoundException e )
            {
                throw new PrincipalNotFoundException( updateUserParams.getKey() );
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
            final Node node = callWithContext( () -> this.nodeService.getById( toNodeId( userKey ) ) );
            return Optional.ofNullable( PrincipalNodeTranslator.userFromNode( node ) );
        }
        catch ( NodeNotFoundException e )
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
        try
        {
            final Node node = callWithContext( () -> this.nodeService.create( createGroupParams ) );

            return PrincipalNodeTranslator.groupFromNode( node );
        }
        catch ( NodeAlreadyExistAtPathException e )
        {
            throw new PrincipalAlreadyExistsException( createGroup.getKey() );
        }
    }

    @Override
    public Group updateGroup( final UpdateGroupParams updateGroupParams )
    {
        return callWithContext( () -> {

            final Node node;
            try
            {
                node = this.nodeService.getById( toNodeId( updateGroupParams.getKey() ) );
            }
            catch ( NodeNotFoundException e )
            {
                throw new PrincipalNotFoundException( updateGroupParams.getKey() );
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
            final Node node = callWithContext( () -> this.nodeService.getById( toNodeId( groupKey ) ) );
            return Optional.ofNullable( PrincipalNodeTranslator.groupFromNode( node ) );
        }
        catch ( NodeNotFoundException e )
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
        try
        {
            final Node node = callWithContext( () -> this.nodeService.create( createNodeParams ) );

            return PrincipalNodeTranslator.roleFromNode( node );
        }
        catch ( NodeAlreadyExistAtPathException e )
        {
            throw new PrincipalAlreadyExistsException( createRole.getKey() );
        }
    }

    @Override
    public Role updateRole( final UpdateRoleParams updateRoleParams )
    {
        return callWithContext( () -> {

            final Node node;
            try
            {
                node = this.nodeService.getById( toNodeId( updateRoleParams.getKey() ) );
            }
            catch ( NodeNotFoundException e )
            {
                throw new PrincipalNotFoundException( updateRoleParams.getKey() );
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
            final Node node = callWithContext( () -> this.nodeService.getById( toNodeId( roleKey ) ) );
            return Optional.ofNullable( PrincipalNodeTranslator.roleFromNode( node ) );
        }
        catch ( NodeNotFoundException e )
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
    public Principals getPrincipals( final PrincipalKeys principalKeys )
    {
        final ImmutableList.Builder<Principal> principals = ImmutableList.builder();
        for ( PrincipalKey key : principalKeys )
        {
            final Node node;
            try
            {
                node = callWithContext( () -> this.nodeService.getById( toNodeId( key ) ) );
            }
            catch ( NodeNotFoundException e )
            {
                continue;
            }

            switch ( key.getType() )
            {
                case USER:
                    principals.add( PrincipalNodeTranslator.userFromNode( node ) );
                    break;
                case GROUP:
                    principals.add( PrincipalNodeTranslator.groupFromNode( node ) );
                    break;
                case ROLE:
                    principals.add( PrincipalNodeTranslator.roleFromNode( node ) );
                    break;
            }

        }
        return Principals.from( principals.build() );
    }

    @Override
    public void deletePrincipal( final PrincipalKey principalKey )
    {
        removeRelationships( principalKey );
        final Node deletedNode = callWithContext( () -> this.nodeService.deleteById( toNodeId( principalKey ) ) );
        if ( deletedNode == null )
        {
            throw new PrincipalNotFoundException( principalKey );
        }
    }

    @Override
    public PrincipalQueryResult query( final PrincipalQuery query )
    {
        try
        {
            final NodeQuery nodeQueryBuilder = PrincipalQueryNodeQueryTranslator.translate( query );
            final FindNodesByQueryResult result = callWithContext( () -> this.nodeService.findByQuery( nodeQueryBuilder ) );

            final Principals principals = PrincipalNodeTranslator.fromNodes( result.getNodes() );
            return PrincipalQueryResult.newResult().
                addPrincipals( principals ).
                totalSize( Ints.checkedCast( result.getTotalHits() ) ).
                build();
        }
        catch ( NodeNotFoundException e )
        {
            return PrincipalQueryResult.newResult().build();
        }
    }

    @Override
    public UserStore createUserStore( final CreateUserStoreParams createUserStoreParams )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( UserStorePropertyNames.DISPLAY_NAME_KEY, createUserStoreParams.getDisplayName() );

        final Node node = callWithContext( () -> {

            final UserStoreAccessControlList permissions = createUserStoreParams.getUserStorePermissions();
            AccessControlList userStoreNodePermissions =
                UserStoreNodeTranslator.userStorePermissionsToUserStoreNodePermissions( permissions );
            AccessControlList usersNodePermissions = UserStoreNodeTranslator.userStorePermissionsToUsersNodePermissions( permissions );
            AccessControlList groupsNodePermissions = UserStoreNodeTranslator.userStorePermissionsToGroupsNodePermissions( permissions );

            final RootNode rootNode = nodeService.getRoot();
            userStoreNodePermissions = mergeWithRootPermissions( userStoreNodePermissions, rootNode.getPermissions() );
            usersNodePermissions = mergeWithRootPermissions( usersNodePermissions, rootNode.getPermissions() );
            groupsNodePermissions = mergeWithRootPermissions( groupsNodePermissions, rootNode.getPermissions() );

            final Node userStoreNode = nodeService.create( CreateNodeParams.create().
                parent( UserStoreNodeTranslator.getUserStoresParentPath() ).
                name( createUserStoreParams.getKey().toString() ).
                data( data ).
                permissions( userStoreNodePermissions ).
                build() );
            final Node usersNode = nodeService.create( CreateNodeParams.create().
                parent( userStoreNode.path() ).
                name( UserStoreNodeTranslator.USER_FOLDER_NODE_NAME ).
                permissions( usersNodePermissions ).
                build() );
            final Node groupsNode = nodeService.create( CreateNodeParams.create().
                parent( userStoreNode.path() ).
                name( UserStoreNodeTranslator.GROUP_FOLDER_NODE_NAME ).
                permissions( groupsNodePermissions ).
                build() );

            final ApplyNodePermissionsParams applyPermissions = ApplyNodePermissionsParams.create().
                nodeId( rootNode.id() ).
                overwriteChildPermissions( false ).
                modifier( ContextAccessor.current().getAuthInfo().getUser().getKey() ).
                build();
            nodeService.applyPermissions( applyPermissions );

            return userStoreNode;
        } );

        return UserStoreNodeTranslator.fromNode( node );
    }

    private AccessControlList mergeWithRootPermissions( final AccessControlList nodePermissions, final AccessControlList rootPermissions )
    {
        final AccessControlList.Builder permissions = AccessControlList.create( nodePermissions );
        for ( PrincipalKey principal : rootPermissions.getAllPrincipals() )
        {
            if ( !nodePermissions.contains( principal ) )
            {
                permissions.add( rootPermissions.getEntry( principal ) );
            }
        }
        return permissions.build();
    }

    @Override
    public UserStore updateUserStore( final UpdateUserStoreParams updateUserStoreParams )
    {
        return callWithContext( () -> {

            final NodePath userStoreNodePath = UserStoreNodeTranslator.toUserStoreNodePath( updateUserStoreParams.getKey() );
            final Node node = this.nodeService.getByPath( userStoreNodePath );
            if ( node == null )
            {
                return null;
            }

            final UpdateNodeParams updateNodeParams = UserStoreNodeTranslator.toUpdateNodeParams( updateUserStoreParams, node.id() );
            final Node userStoreNode = nodeService.update( updateNodeParams );

            if ( updateUserStoreParams.getUserStorePermissions() != null )
            {
                final Node usersNode =
                    nodeService.getByPath( UserStoreNodeTranslator.toUserStoreUsersNodePath( updateUserStoreParams.getKey() ) );
                final Node groupsNode =
                    nodeService.getByPath( UserStoreNodeTranslator.toUserStoreGroupsNodePath( updateUserStoreParams.getKey() ) );

                final UserStoreAccessControlList permissions = updateUserStoreParams.getUserStorePermissions();
                AccessControlList userStoreNodePermissions =
                    UserStoreNodeTranslator.userStorePermissionsToUserStoreNodePermissions( permissions );
                AccessControlList usersNodePermissions = UserStoreNodeTranslator.userStorePermissionsToUsersNodePermissions( permissions );
                AccessControlList groupsNodePermissions =
                    UserStoreNodeTranslator.userStorePermissionsToGroupsNodePermissions( permissions );

                final RootNode rootNode = nodeService.getRoot();
                userStoreNodePermissions = mergeWithRootPermissions( userStoreNodePermissions, rootNode.getPermissions() );
                usersNodePermissions = mergeWithRootPermissions( usersNodePermissions, rootNode.getPermissions() );
                groupsNodePermissions = mergeWithRootPermissions( groupsNodePermissions, rootNode.getPermissions() );

                setNodePermissions( userStoreNode.id(), userStoreNodePermissions );
                setNodePermissions( usersNode.id(), usersNodePermissions );
                setNodePermissions( groupsNode.id(), groupsNodePermissions );

                final ApplyNodePermissionsParams applyPermissions = ApplyNodePermissionsParams.create().
                    nodeId( userStoreNode.id() ).
                    overwriteChildPermissions( false ).
                    modifier( ContextAccessor.current().getAuthInfo().getUser().getKey() ).
                    build();
                nodeService.applyPermissions( applyPermissions );
            }

            return UserStoreNodeTranslator.fromNode( userStoreNode );
        } );
    }

    private void setNodePermissions( final NodeId nodeId, final AccessControlList permissions )
    {
        final UpdateNodeParams updateParams = UpdateNodeParams.create().
            id( nodeId ).
            editor( editableNode -> editableNode.permissions = permissions ).
            build();

        nodeService.update( updateParams );
    }

    private <T> T callWithContext( Callable<T> runnable )
    {
        return this.getContext().callWith( runnable );
    }

    private Context getContext()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return ContextBuilder.from( CONTEXT_SECURITY ).authInfo( authInfo ).build();
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    public void setClock( final Clock clock )
    {
        this.clock = clock;
    }
}
