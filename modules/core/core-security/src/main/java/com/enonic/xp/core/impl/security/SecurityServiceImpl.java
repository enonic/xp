package com.enonic.xp.core.impl.security;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Striped;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIdExistsException;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.LogicalExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.security.CreateGroupParams;
import com.enonic.xp.security.CreateIdProviderParams;
import com.enonic.xp.security.CreateRoleParams;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.security.IdProviderAlreadyExistsException;
import com.enonic.xp.security.IdProviderConfig;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderNotFoundException;
import com.enonic.xp.security.IdProviders;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalAlreadyExistsException;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalNotFoundException;
import com.enonic.xp.security.PrincipalQuery;
import com.enonic.xp.security.PrincipalQueryResult;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.UpdateGroupParams;
import com.enonic.xp.security.UpdateIdProviderParams;
import com.enonic.xp.security.UpdateRoleParams;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserQuery;
import com.enonic.xp.security.UserQueryResult;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.IdProviderAccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.security.auth.PasswordAuthToken;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;
import com.enonic.xp.security.auth.VerifiedEmailAuthToken;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;

public final class SecurityServiceImpl
    implements SecurityService
{
    private static final Set<PrincipalKey> FORBIDDEN_FROM_RELATIONSHIP = Set.of( RoleKeys.EVERYONE, RoleKeys.AUTHENTICATED );

    private static final List<PrincipalKey> NON_REMOVABLE_PRINCIPLES = List.of( PrincipalKey.ofSuperUser(), RoleKeys.ADMIN );

    static Clock clock = Clock.systemUTC();

    private final PasswordSecurityService passwordSecurityService;

    private final Striped<Lock> userEmailLocks = Striped.lazyWeakLock( 100 );

    private final NodeService nodeService;

    private final SecurityAuditLogSupport securityAuditLogSupport;

    public SecurityServiceImpl( final NodeService nodeService, final SecurityAuditLogSupport securityAuditLogSupport,
                                PasswordSecurityService passwordSecurityService )
    {
        this.nodeService = nodeService;
        this.securityAuditLogSupport = securityAuditLogSupport;
        this.passwordSecurityService = passwordSecurityService;
    }

    @Override
    public IdProviders getIdProviders()
    {
        final FindNodesByParentParams findByParent =
            FindNodesByParentParams.create().parentPath( IdProviderNodeTranslator.ID_PROVIDERS_PARENT_PATH ).build();
        final Nodes nodes = callWithContext( () -> {
            final FindNodesByParentResult result = this.nodeService.findByParent( findByParent );
            return this.nodeService.getByIds( result.getNodeIds() );
        } );

        return IdProviderNodeTranslator.fromNodes( nodes );
    }

    @Override
    public IdProvider getIdProvider( final IdProviderKey idProviderKey )
    {
        final NodePath idProviderNodePath = IdProviderNodeTranslator.toIdProviderNodePath( idProviderKey );
        final Node node = callWithContext( () -> this.nodeService.getByPath( idProviderNodePath ) );
        return node == null ? null : IdProviderNodeTranslator.fromNode( node );
    }

    @Override
    public IdProviderAccessControlList getIdProviderPermissions( final IdProviderKey idProviderKey )
    {
        final NodePath idProviderNodePath = IdProviderNodeTranslator.toIdProviderNodePath( idProviderKey );
        final NodePath usersNodePath = IdProviderNodeTranslator.toIdProviderUsersNodePath( idProviderKey );
        final NodePath groupsNodePath = IdProviderNodeTranslator.toIdProviderGroupsNodePath( idProviderKey );

        final Node idProviderNode = callWithContext( () -> this.nodeService.getByPath( idProviderNodePath ) );
        final Node usersNode = callWithContext( () -> this.nodeService.getByPath( usersNodePath ) );
        final Node groupsNode = callWithContext( () -> this.nodeService.getByPath( groupsNodePath ) );

        return IdProviderNodeTranslator.idProviderPermissionsFromNode( idProviderNode, usersNode, groupsNode );
    }

    @Override
    public PrincipalRelationships getRelationships( final PrincipalKey from )
    {
        final Node node = callWithContext( () -> this.nodeService.getByPath( from.toPath() ) );
        return node == null ? PrincipalRelationships.empty() : PrincipalNodeTranslator.relationshipsFromNode( node );
    }

    @Override
    public void addRelationship( final PrincipalRelationship relationship )
    {
        final PrincipalKey from = relationship.getFrom();
        if ( FORBIDDEN_FROM_RELATIONSHIP.contains( from ) )
        {
            throw new IllegalArgumentException( "Invalid 'from' value in relationship: " + from );
        }
        callWithContext( () -> {
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.addRelationshipToUpdateNodeParams( relationship );
            nodeService.update( updateNodeParams );

            securityAuditLogSupport.addRelationship( relationship );

            return null;
        } );
    }

    @Override
    public void removeRelationship( final PrincipalRelationship relationship )
    {
        if ( RoleKeys.ADMIN.equals( relationship.getFrom() ) && PrincipalKey.ofSuperUser().equals( relationship.getTo() ) )
        {
            throw new IllegalArgumentException( "Super user cannot be removed from the administrator role" );
        }

        callWithContext( () -> {
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.removeRelationshipToUpdateNodeParams( relationship );
            nodeService.update( updateNodeParams );

            securityAuditLogSupport.removeRelationship( relationship );

            return null;
        } );
    }

    public void removeRelationships( final PrincipalKey from )
    {
        callWithContext( () -> {
            nodeService.update( PrincipalNodeTranslator.removeAllRelationshipsToUpdateNodeParams( from ) );

            securityAuditLogSupport.removeRelationships( from );

            return null;
        } );
    }

    private void doRemoveMemberships( final PrincipalKey member )
    {
        final PrincipalKeys memberships = queryDirectMemberships( member );
        if ( memberships.isEmpty() )
        {
            return;
        }

        for ( PrincipalKey from : memberships )
        {
            final PrincipalRelationship relationship = PrincipalRelationship.from( from ).to( member );
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.removeRelationshipToUpdateNodeParams( relationship );
            nodeService.update( updateNodeParams );
        }
    }

    private PrincipalKeys resolveMemberships( final PrincipalKey userKey )
    {
        final PrincipalKeys directMemberships = queryDirectMemberships( userKey );
        final Set<PrincipalKey> resolvedMemberships = new LinkedHashSet<>( directMemberships.getSet() );

        final Set<PrincipalKey> queriedMemberships = new LinkedHashSet<>();

        do
        {
            final Set<PrincipalKey> newMemberships = new LinkedHashSet<>();
            resolvedMemberships.stream().filter( principal -> !queriedMemberships.contains( principal ) ).forEach( principal -> {
                final PrincipalKeys indirectMemberships = queryDirectMemberships( principal );
                newMemberships.addAll( indirectMemberships.getSet() );
                queriedMemberships.add( principal );
            } );
            resolvedMemberships.addAll( newMemberships );
        }
        while ( resolvedMemberships.size() > queriedMemberships.size() );

        return PrincipalKeys.from( resolvedMemberships );
    }

    private PrincipalKeys queryDirectMemberships( final PrincipalKey member )
    {
        try
        {
            final Nodes nodes = callWithContext( () -> {
                final FindNodesByQueryResult result = this.nodeService.findByQuery( NodeQuery.create()
                                                                                        .addQueryFilter( ValueFilter.create()
                                                                                                             .fieldName(
                                                                                                                 PrincipalPropertyNames.MEMBER_KEY )
                                                                                                             .addValue(
                                                                                                                 ValueFactory.newString(
                                                                                                                     member.toString() ) )
                                                                                                             .build() )
                                                                                        .size( NodeQuery.ALL_RESULTS_SIZE_FLAG )
                                                                                        .build() );
                return this.nodeService.getByIds( result.getNodeIds() );
            } );
            return PrincipalKeyNodeTranslator.fromNodes( nodes );
        }
        catch ( NodeNotFoundException e )
        {
            return PrincipalKeys.empty();
        }
    }

    @Override
    public PrincipalKeys getMemberships( final PrincipalKey principalKey )
    {
        return queryDirectMemberships( principalKey );
    }

    @Override
    public PrincipalKeys getAllMemberships( PrincipalKey principalKey )
    {
        return resolveMemberships( principalKey );
    }

    @Override
    public AuthenticationInfo authenticate( final AuthenticationToken token )
    {
        if ( token instanceof UsernamePasswordAuthToken usernamePasswordAuthToken &&
            PrincipalKey.ofSuperUser().getIdProviderKey().equals( usernamePasswordAuthToken.getIdProvider() ) &&
            PrincipalKey.ofSuperUser().getId().equals( usernamePasswordAuthToken.getUsername() ) )
        {
            return authenticateSu( usernamePasswordAuthToken );
        }

        return doAuthenticate( token );
    }

    private AuthenticationInfo authenticateSu( final UsernamePasswordAuthToken authToken )
    {
        if ( !authToken.getPassword().isEmpty() &&
            passwordSecurityService.suPasswordValidator().validate( authToken.getPassword().toCharArray() ) )
        {
            final User admin = User.create()
                .key( PrincipalKey.ofSuperUser() )
                .login( PrincipalKey.ofSuperUser().getId() )
                .displayName( "Super User" )
                .build();
            return AuthenticationInfo.create()
                .principals( RoleKeys.ADMIN, RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE )
                .user( admin )
                .build();
        }
        else
        {
            return AuthenticationInfo.unAuthenticated();
        }
    }

    private AuthenticationInfo doAuthenticate( final AuthenticationToken token )
    {
        return callAsAuthenticated( () -> {
            final IndexPath principalField;
            final String principalValue;
            final Predicate<User> credentialsVerifier;
            switch ( token )
            {
                case PasswordAuthToken authToken ->
                {
                    if ( authToken.getPassword().isEmpty() )
                    {
                        // Empty passwords are not worth checking
                        return AuthenticationInfo.unAuthenticated();
                    }
                    switch ( authToken )
                    {
                        case UsernamePasswordAuthToken usernamePasswordAuthToken ->
                        {
                            principalField = PrincipalIndexPath.LOGIN_KEY;
                            principalValue = usernamePasswordAuthToken.getUsername();
                        }
                        case EmailPasswordAuthToken emailPasswordAuthToken ->
                        {
                            principalField = PrincipalIndexPath.EMAIL_KEY;
                            principalValue = emailPasswordAuthToken.getEmail();
                        }
                    }
                    credentialsVerifier = verifyUserPassword( authToken.getPassword() );
                }
                case VerifiedUsernameAuthToken authToken ->
                {
                    principalField = PrincipalIndexPath.LOGIN_KEY;
                    principalValue = authToken.getUsername();
                    credentialsVerifier = _ -> true;
                }
                case VerifiedEmailAuthToken authToken ->
                {
                    principalField = PrincipalIndexPath.EMAIL_KEY;
                    principalValue = authToken.getEmail();
                    credentialsVerifier = _ -> true;
                }
            }
            final User user = findPrincipalByField( principalField, principalValue, token.getIdProvider() );

            // Order matters. First verify the password, then check for non-existing/disabled user to avoid timing attacks
            if ( credentialsVerifier.test( user ) && user != null && !user.isDisabled() )
            {
                return createAuthInfo( user );
            }
            else
            {
                return AuthenticationInfo.unAuthenticated();
            }
        } );
    }

    private Predicate<User> verifyUserPassword( final String password )
    {
        return user -> passwordSecurityService.validatorFor( Strings.nullToEmpty( user != null ? user.getAuthenticationHash() : "" ) )
            .validate( password.toCharArray() );
    }

    private AuthenticationInfo createAuthInfo( final User user )
    {
        final PrincipalKeys principals = resolveMemberships( user.getKey() );
        return AuthenticationInfo.create()
            .principals( principals )
            .principals( RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE )
            .user( user )
            .build();
    }

    private User findPrincipalByField( final IndexPath field, final String value, final IdProviderKey idProvider )
    {
        final CompareExpr idProviderExpr =
            CompareExpr.create( FieldExpr.from( PrincipalIndexPath.ID_PROVIDER_KEY ), CompareExpr.Operator.EQ,
                                ValueExpr.string( idProvider.toString() ) );
        final CompareExpr userNameExpr = CompareExpr.create( FieldExpr.from( field ), CompareExpr.Operator.EQ, ValueExpr.string( value ) );
        final QueryExpr query = QueryExpr.from( LogicalExpr.and( idProviderExpr, userNameExpr ) );
        final Nodes nodes = callWithContext( () -> {
            final FindNodesByQueryResult result = nodeService.findByQuery( NodeQuery.create().query( query ).build() );
            return this.nodeService.getByIds( result.getNodeIds() );
        } );

        if ( nodes.getSize() > 1 )
        {
            throw new IllegalArgumentException( "Expected at most 1 user with " + field + " " + value + " in id provider " + idProvider );
        }

        return nodes.isEmpty() ? null : PrincipalNodeTranslator.userFromNode( nodes.first() );
    }

    @Override
    public User setPassword( final PrincipalKey key, final String password )
    {
        Preconditions.checkArgument( key.isUser(), "Expected principal key of type User" );

        return callWithContext( () -> {
            final Node node = callWithContext( () -> this.nodeService.getByPath( key.toPath() ) );
            if ( node == null )
            {
                throw new NodeNotFoundException( "setPassword failed, user with key " + key + " not found" );
            }

            final User user = PrincipalNodeTranslator.userFromNode( node );

            final String authenticationHash =
                password != null ? this.passwordSecurityService.defaultEncoder().encode( password.toCharArray() ) : null;

            final User userToUpdate = User.create( user ).authenticationHash( authenticationHash ).build();

            final Node updatedNode = nodeService.update( PrincipalNodeTranslator.toUpdateNodeParams( userToUpdate ) );

            securityAuditLogSupport.setPassword( key );

            return PrincipalNodeTranslator.userFromNode( updatedNode );
        } );
    }

    private User doCreateUser( final CreateUserParams createUser )
    {
        final User user = User.create()
            .key( createUser.getKey() )
            .login( createUser.getLogin() )
            .email( createUser.getEmail() )
            .displayName( createUser.getDisplayName() )
            .modifiedTime( Instant.now( clock ) )
            .build();

        final CreateNodeParams createNodeParams = PrincipalNodeTranslator.toCreateNodeParams( user );
        try
        {
            final Node node = callWithContext( () -> nodeService.create( createNodeParams ) );

            if ( createUser.getPassword() != null )
            {
                return setPassword( user.getKey(), createUser.getPassword() );
            }

            securityAuditLogSupport.createUser( createUser );

            return PrincipalNodeTranslator.userFromNode( node );
        }
        catch ( NodeIdExistsException | NodeAlreadyExistAtPathException e )
        {
            throw new PrincipalAlreadyExistsException( createUser.getKey() );
        }
    }

    private String idProviderEmailKey( final PrincipalKey principalKey, final String email )
    {
        return principalKey.getIdProviderKey().toString() + '|' + email;
    }

    @Override
    public User createUser( final CreateUserParams createUser )
    {
        final Lock lock = userEmailLocks.get( idProviderEmailKey( createUser.getKey(), createUser.getEmail() ) );
        lock.lock();
        try
        {
            duplicateEmailValidation( createUser.getKey(), createUser.getEmail() );
            return doCreateUser( createUser );
        }
        finally
        {
            lock.unlock();
        }
    }

    private User doUpdateUser( final UpdateUserParams updateUserParams )
    {
        return callWithContext( () -> {

            final Node node = this.nodeService.getByPath( updateUserParams.getKey().toPath() );
            if ( node == null )
            {
                throw new PrincipalNotFoundException( updateUserParams.getKey() );
            }

            final User existingUser = PrincipalNodeTranslator.userFromNode( node );

            final User userToUpdate = updateUserParams.update( existingUser );
            duplicateEmailValidation( userToUpdate.getKey(), userToUpdate.getEmail() );

            final Node updatedNode = nodeService.update( PrincipalNodeTranslator.toUpdateNodeParams( userToUpdate ) );

            securityAuditLogSupport.updateUser( UpdateUserParams.create( userToUpdate ).build() );

            return PrincipalNodeTranslator.userFromNode( updatedNode );
        } );

    }

    @Override
    public User updateUser( final UpdateUserParams updateUserParams )
    {
        final String key = idProviderEmailKey( updateUserParams.getKey(), updateUserParams.getEmail() );
        final Lock lock = userEmailLocks.get( key );
        lock.lock();
        try
        {
            return doUpdateUser( updateUserParams );
        }
        finally
        {
            lock.unlock();
        }
    }

    private void duplicateEmailValidation( final PrincipalKey key, final String email )
    {
        if ( email == null )
        {
            return;
        }

        final CompareExpr idProviderExpr =
            CompareExpr.create( FieldExpr.from( PrincipalIndexPath.ID_PROVIDER_KEY ), CompareExpr.Operator.EQ,
                                ValueExpr.string( key.getIdProviderKey().toString() ) );
        final CompareExpr emailExpr =
            CompareExpr.create( FieldExpr.from( PrincipalIndexPath.EMAIL_KEY ), CompareExpr.Operator.EQ, ValueExpr.string( email ) );
        final QueryExpr query = QueryExpr.from( LogicalExpr.and( idProviderExpr, emailExpr ) );
        final Nodes nodes = callWithContext( () -> {
            final FindNodesByQueryResult result = nodeService.findByQuery( NodeQuery.create().query( query ).build() );
            return this.nodeService.getByIds( result.getNodeIds() );
        } );

        final User user = nodes.isEmpty() ? null : PrincipalNodeTranslator.userFromNode( nodes.first() );

        if ( nodes.getSize() > 1 || ( user != null && !user.getKey().equals( key ) ) )
        {
            throw new IllegalArgumentException(
                "A user with email '" + email + "' already exists in id provider '" + key.getIdProviderKey() + "'" );
        }
    }

    @Override
    public Optional<User> getUser( final PrincipalKey userKey )
    {
        Preconditions.checkArgument( userKey.isUser(), "Expected principal key of type User" );

        final Node node = callWithContext( () -> this.nodeService.getByPath( userKey.toPath() ) );
        return Optional.ofNullable( node ).map( PrincipalNodeTranslator::userFromNode );
    }

    @Override
    public Group createGroup( final CreateGroupParams createGroup )
    {
        final Group group = Group.create()
            .key( createGroup.getKey() )
            .displayName( createGroup.getDisplayName() )
            .modifiedTime( Instant.now( clock ) )
            .description( createGroup.getDescription() )
            .build();

        final CreateNodeParams createGroupParams = PrincipalNodeTranslator.toCreateNodeParams( group );
        try
        {
            final Node node = callWithContext( () -> this.nodeService.create( createGroupParams ) );

            securityAuditLogSupport.createGroup( createGroup );

            return PrincipalNodeTranslator.groupFromNode( node );
        }
        catch ( NodeIdExistsException | NodeAlreadyExistAtPathException e )
        {
            throw new PrincipalAlreadyExistsException( createGroup.getKey() );
        }
    }

    @Override
    public Group updateGroup( final UpdateGroupParams updateGroupParams )
    {
        return callWithContext( () -> {

            final Node node = this.nodeService.getByPath( updateGroupParams.getKey().toPath() );
            if ( node == null )
            {
                throw new PrincipalNotFoundException( updateGroupParams.getKey() );
            }

            final Group existingGroup = PrincipalNodeTranslator.groupFromNode( node );

            final Group groupToUpdate = updateGroupParams.update( existingGroup );

            final Node updatedNode = nodeService.update( PrincipalNodeTranslator.toUpdateNodeParams( groupToUpdate ) );

            securityAuditLogSupport.updateGroup( UpdateGroupParams.create( groupToUpdate ).build() );

            return PrincipalNodeTranslator.groupFromNode( updatedNode );
        } );
    }

    @Override
    public Optional<Group> getGroup( final PrincipalKey groupKey )
    {
        Preconditions.checkArgument( groupKey.isGroup(), "Expected principal key of type Group" );

        final Node node = callWithContext( () -> this.nodeService.getByPath( groupKey.toPath() ) );
        return Optional.ofNullable( node ).map( PrincipalNodeTranslator::groupFromNode );
    }

    @Override
    public Role createRole( final CreateRoleParams createRole )
    {
        final Role role = Role.create()
            .key( createRole.getKey() )
            .displayName( createRole.getDisplayName() )
            .modifiedTime( Instant.now( clock ) )
            .description( createRole.getDescription() )
            .build();

        final CreateNodeParams createNodeParams = PrincipalNodeTranslator.toCreateNodeParams( role );
        try
        {
            final Node node = callWithContext( () -> this.nodeService.create( createNodeParams ) );

            securityAuditLogSupport.createRole( createRole );

            return PrincipalNodeTranslator.roleFromNode( node );
        }
        catch ( NodeIdExistsException | NodeAlreadyExistAtPathException e )
        {
            throw new PrincipalAlreadyExistsException( createRole.getKey() );
        }
    }

    @Override
    public Role updateRole( final UpdateRoleParams updateRoleParams )
    {
        return callWithContext( () -> {

            final Node node = this.nodeService.getByPath( updateRoleParams.getKey().toPath() );
            if ( node == null )
            {
                throw new PrincipalNotFoundException( updateRoleParams.getKey() );
            }

            final Role existingRole = PrincipalNodeTranslator.roleFromNode( node );

            final Role roleToUpdate = updateRoleParams.update( existingRole );

            final Node updatedNode = nodeService.update( PrincipalNodeTranslator.toUpdateNodeParams( roleToUpdate ) );

            securityAuditLogSupport.updateRole( UpdateRoleParams.create( roleToUpdate ).build() );

            return PrincipalNodeTranslator.roleFromNode( updatedNode );
        } );
    }

    @Override
    public Optional<Role> getRole( final PrincipalKey roleKey )
    {
        Preconditions.checkArgument( roleKey.isRole(), "Expected principal key of type Role" );

        final Node node = callWithContext( () -> this.nodeService.getByPath( roleKey.toPath() ) );
        return Optional.ofNullable( node ).map( PrincipalNodeTranslator::roleFromNode );
    }

    @Override
    public Optional<? extends Principal> getPrincipal( final PrincipalKey principalKey )
    {
        return switch ( Objects.requireNonNull( principalKey, "Principal key was null" ).getType() )
        {
            case USER -> getUser( principalKey );
            case GROUP -> getGroup( principalKey );
            case ROLE -> getRole( principalKey );
        };
    }

    @Override
    public Principals getPrincipals( final PrincipalKeys principalKeys )
    {
        final ImmutableList.Builder<Principal> principals = ImmutableList.builder();
        for ( PrincipalKey key : principalKeys )
        {
            final Node node = callWithContext( () -> this.nodeService.getByPath( key.toPath() ) );
            if ( node == null )
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
    public void deleteIdProvider( final IdProviderKey idProviderKey )
    {
        final NodeIds deletedNodes;
        try
        {
            deletedNodes = callWithContext( () -> {
                final NodePath idProviderNodePath = IdProviderNodeTranslator.toIdProviderNodePath( idProviderKey );
                return this.nodeService.delete(
                    DeleteNodeParams.create().nodePath( idProviderNodePath ).refresh( RefreshMode.ALL ).build() ).getNodeIds();
            } );
        }
        catch ( NodeAccessException e )
        {
            throw new IdProviderNotFoundException( idProviderKey );
        }
        if ( deletedNodes.isEmpty() )
        {
            throw new IdProviderNotFoundException( idProviderKey );
        }
        securityAuditLogSupport.removeIdProvider( idProviderKey );
    }

    @Override
    public void deletePrincipal( final PrincipalKey principalKey )
    {
        if ( NON_REMOVABLE_PRINCIPLES.contains( principalKey ) )
        {
            throw new IllegalArgumentException( String.format( "[%s] principal cannot be removed", principalKey ) );
        }

        final NodeIds deletedNodes;
        try
        {
            deletedNodes = callWithContext( () -> {
                nodeService.update( PrincipalNodeTranslator.removeAllRelationshipsToUpdateNodeParams( principalKey ) );
                doRemoveMemberships( principalKey );

                return this.nodeService.delete(
                    DeleteNodeParams.create().nodePath( principalKey.toPath() ).refresh( RefreshMode.ALL ).build() ).getNodeIds();
            } );
        }
        catch ( NodeNotFoundException e ) // catch doRemoveRelationships and doRemoveMemberships leak of permissions
        {
            throw new PrincipalNotFoundException( principalKey );
        }

        if ( deletedNodes.isEmpty() )
        {
            throw new PrincipalNotFoundException( principalKey );
        }

        securityAuditLogSupport.removePrincipal( principalKey );
    }

    @Override
    public PrincipalQueryResult query( final PrincipalQuery query )
    {
        try
        {
            final NodeQuery nodeQueryBuilder = PrincipalQueryNodeQueryTranslator.translate( query );
            final FindNodesByQueryResult result = callWithContext( () -> this.nodeService.findByQuery( nodeQueryBuilder ) );
            final Nodes nodes = callWithContext( () -> this.nodeService.getByIds( result.getNodeIds() ) );

            final Principals principals = PrincipalNodeTranslator.fromNodes( nodes );
            return PrincipalQueryResult.create().addPrincipals( principals ).totalSize( Math.toIntExact( result.getTotalHits() ) ).build();
        }
        catch ( NodeNotFoundException e )
        {
            return PrincipalQueryResult.create().build();
        }
    }

    @Override
    public UserQueryResult query( final UserQuery query )
    {
        try
        {
            final NodeQuery nodeQueryBuilder = UserQueryNodeQueryTranslator.translate( query );
            final FindNodesByQueryResult result = callWithContext( () -> this.nodeService.findByQuery( nodeQueryBuilder ) );
            final Nodes nodes = callWithContext( () -> this.nodeService.getByIds( result.getNodeIds() ) );

            final Principals principals = PrincipalNodeTranslator.fromNodes( nodes );
            return UserQueryResult.create().addUsers( principals ).totalSize( Math.toIntExact( result.getTotalHits() ) ).build();
        }
        catch ( NodeNotFoundException e )
        {
            return UserQueryResult.create().build();
        }
    }

    @Override
    public IdProvider createIdProvider( final CreateIdProviderParams createIdProviderParams )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( IdProviderPropertyNames.DISPLAY_NAME_KEY, createIdProviderParams.getDisplayName() );
        data.setString( IdProviderPropertyNames.DESCRIPTION_KEY, createIdProviderParams.getDescription() );
        final IdProviderConfig idProviderConfig = createIdProviderParams.getIdProviderConfig();
        if ( idProviderConfig != null )
        {
            data.setString( IdProviderPropertyNames.ID_PROVIDER_APPLICATION_KEY, idProviderConfig.getApplicationKey().toString() );
            data.setSet( IdProviderPropertyNames.ID_PROVIDER_CONFIG_FORM_KEY, idProviderConfig.getConfig().getRoot().copy( data ) );
        }

        try
        {
            final Node node = callWithContext( () -> {

                final IdProviderAccessControlList permissions = createIdProviderParams.getIdProviderPermissions();
                AccessControlList idProviderNodePermissions =
                    IdProviderNodeTranslator.idProviderPermissionsToIdProviderNodePermissions( permissions );
                AccessControlList usersNodePermissions =
                    IdProviderNodeTranslator.idProviderPermissionsToUsersNodePermissions( permissions );
                AccessControlList groupsNodePermissions =
                    IdProviderNodeTranslator.idProviderPermissionsToGroupsNodePermissions( permissions );

                final Node rootNode = nodeService.getById( Node.ROOT_UUID );
                idProviderNodePermissions = mergeWithRootPermissions( idProviderNodePermissions, rootNode.getPermissions() );
                usersNodePermissions = mergeWithRootPermissions( usersNodePermissions, rootNode.getPermissions() );
                groupsNodePermissions = mergeWithRootPermissions( groupsNodePermissions, rootNode.getPermissions() );

                final Node idProviderNode = nodeService.create( CreateNodeParams.create()
                                                                    .parent( IdProviderNodeTranslator.ID_PROVIDERS_PARENT_PATH )
                                                                    .name( createIdProviderParams.getKey().toString() )
                                                                    .data( data )
                                                                    .permissions( idProviderNodePermissions )
                                                                    .build() );

                nodeService.create( CreateNodeParams.create()
                                        .parent( idProviderNode.path() )
                                        .name( IdProviderNodeTranslator.USER_FOLDER_NODE_NAME )
                                        .permissions( usersNodePermissions )
                                        .build() );
                nodeService.create( CreateNodeParams.create()
                                        .parent( idProviderNode.path() )
                                        .name( IdProviderNodeTranslator.GROUP_FOLDER_NODE_NAME )
                                        .permissions( groupsNodePermissions )
                                        .build() );

                return idProviderNode;
            } );

            securityAuditLogSupport.createIdProvider( createIdProviderParams );

            return IdProviderNodeTranslator.fromNode( node );
        }
        catch ( NodeIdExistsException | NodeAlreadyExistAtPathException e )
        {
            throw new IdProviderAlreadyExistsException( createIdProviderParams.getKey() );
        }
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
    public IdProvider updateIdProvider( final UpdateIdProviderParams updateIdProviderParams )
    {
        return callWithContext( () -> {

            final NodePath idProviderNodePath = IdProviderNodeTranslator.toIdProviderNodePath( updateIdProviderParams.getKey() );
            final Node node = this.nodeService.getByPath( idProviderNodePath );
            if ( node == null )
            {
                return null;
            }

            final IdProvider existingIdProvider = IdProviderNodeTranslator.fromNode( node );

            final IdProvider idProviderToUpdate = updateIdProviderParams.update( existingIdProvider );

            final UpdateNodeParams updateNodeParams = IdProviderNodeTranslator.toUpdateNodeParams( idProviderToUpdate, node.id() );
            final Node idProviderNode = nodeService.update( updateNodeParams );

            if ( updateIdProviderParams.getIdProviderPermissions() != null )
            {
                final Node usersNode =
                    nodeService.getByPath( IdProviderNodeTranslator.toIdProviderUsersNodePath( updateIdProviderParams.getKey() ) );
                final Node groupsNode =
                    nodeService.getByPath( IdProviderNodeTranslator.toIdProviderGroupsNodePath( updateIdProviderParams.getKey() ) );

                final IdProviderAccessControlList permissions = updateIdProviderParams.getIdProviderPermissions();
                AccessControlList idProviderNodePermissions =
                    IdProviderNodeTranslator.idProviderPermissionsToIdProviderNodePermissions( permissions );
                AccessControlList usersNodePermissions =
                    IdProviderNodeTranslator.idProviderPermissionsToUsersNodePermissions( permissions );
                AccessControlList groupsNodePermissions =
                    IdProviderNodeTranslator.idProviderPermissionsToGroupsNodePermissions( permissions );

                final Node rootNode = nodeService.getById( Node.ROOT_UUID );
                idProviderNodePermissions = mergeWithRootPermissions( idProviderNodePermissions, rootNode.getPermissions() );
                usersNodePermissions = mergeWithRootPermissions( usersNodePermissions, rootNode.getPermissions() );
                groupsNodePermissions = mergeWithRootPermissions( groupsNodePermissions, rootNode.getPermissions() );

                setNodePermissions( idProviderNode.id(), idProviderNodePermissions );
                setNodePermissions( usersNode.id(), usersNodePermissions );
                setNodePermissions( groupsNode.id(), groupsNodePermissions );
            }

            securityAuditLogSupport.updateIdProvider( UpdateIdProviderParams.create( idProviderToUpdate ).build() );

            return IdProviderNodeTranslator.fromNode( idProviderNode );
        } );
    }

    private void setNodePermissions( final NodeId nodeId, final AccessControlList permissions )
    {
        nodeService.applyPermissions( ApplyNodePermissionsParams.create().nodeId( nodeId ).permissions( permissions ).build() );
        nodeService.refresh( RefreshMode.ALL );
    }

    private <T> T callWithContext( Callable<T> runnable )
    {
        return this.getContext().callWith( runnable );
    }

    private Context getContext()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return ContextBuilder.create()
            .branch( SecurityConstants.BRANCH_SECURITY )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .authInfo( authInfo )
            .build();
    }

    private <T> T callAsAuthenticated( Callable<T> runnable )
    {
        return this.getAuthenticatedContext().callWith( runnable );
    }

    private Context getAuthenticatedContext()
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED ).user( User.anonymous() ).build();
        return ContextBuilder.create()
            .branch( SecurityConstants.BRANCH_SECURITY )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .authInfo( authInfo )
            .build();
    }
}
