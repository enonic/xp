package com.enonic.xp.core.impl.security;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.internal.Millis;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
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
import com.enonic.xp.security.RoleRestriction;
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
import com.enonic.xp.core.impl.security.token.ClientStore;
import com.enonic.xp.core.impl.security.token.ScopeStore;
import com.enonic.xp.security.token.Client;
import com.enonic.xp.security.token.Clients;
import com.enonic.xp.security.token.CreateClientParams;
import com.enonic.xp.security.token.CreateScopeParams;
import com.enonic.xp.security.token.Scope;
import com.enonic.xp.security.token.UpdateClientParams;
import com.enonic.xp.security.token.Scopes;
import com.enonic.xp.security.token.UpdateScopeParams;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.security.auth.PasswordAuthToken;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;
import com.enonic.xp.security.auth.VerifiedEmailAuthToken;
import com.enonic.xp.security.auth.VerifiedUserKeyAuthToken;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;
import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;

import static com.enonic.xp.core.impl.security.SharedLocks.withLock;
import static java.util.Objects.requireNonNull;

public final class SecurityServiceImpl
    implements SecurityService
{
    private static final Set<PrincipalKey> FORBIDDEN_FROM_RELATIONSHIP = Set.of( RoleKeys.EVERYONE, RoleKeys.AUTHENTICATED );

    private static final Set<NodePath> NON_REMOVABLE_PRINCIPAL_PATHS =
        Stream.concat( Stream.of( PrincipalKey.ofSuperUser(), PrincipalKey.ofAnonymous() ), SecurityInitializer.SYSTEM_ROLES.stream() )
            .map( PrincipalKey::toPath )
            .collect( Collectors.toUnmodifiableSet() );

    /**
     * Role namespaces owned by the platform and by Content Studio. Only an administrator creates roles there, so a role cannot be
     * squatted before the platform creates it (a project's roles are adopted together with their members).
     */
    private static final List<String> RESERVED_ROLE_PREFIXES = List.of( "system.", "cms." );

    private static final String PROJECT_ROLE_PREFIX = "cms.project.";

    /**
     * Principals under an id provider are stored with their lower-cased key as node id, which allows only {@code [a-z0-9_.:-]}.
     * A dot is refused as well: the key becomes a level of the vhost configuration ({@code mapping.<name>.idProvider.<key>}),
     * where a dot makes nested settings ambiguous. Existing keys with a dot keep working.
     */
    private static final Pattern ID_PROVIDER_KEY_PATTERN = Pattern.compile( "[A-Za-z0-9_-]+" );

    private static final Set<NodePath> NON_MODIFIABLE_USER_PATHS =
        Set.of( PrincipalKey.ofSuperUser().toPath(), PrincipalKey.ofAnonymous().toPath() );

    static final String ID_PROVIDER_LOCKS = "com.enonic.xp.security.lock.idprovider";

    private final PasswordSecurityService passwordSecurityService;

    private final SharedMap<String, Boolean> idProviderLocks;

    private final NodeService nodeService;

    private final SecurityAuditLogSupport securityAuditLogSupport;

    private final ScopeStore scopes;

    private final ClientStore clients;

    public SecurityServiceImpl( final NodeService nodeService, final SecurityAuditLogSupport securityAuditLogSupport,
                                final PasswordSecurityService passwordSecurityService, final SharedMapService sharedMapService )
    {
        this.nodeService = nodeService;
        this.idProviderLocks = sharedMapService.getSharedMap( ID_PROVIDER_LOCKS );
        this.securityAuditLogSupport = securityAuditLogSupport;
        this.passwordSecurityService = passwordSecurityService;
        this.scopes = new ScopeStore( nodeService );
        this.clients = new ClientStore( nodeService );
    }

    @Override
    public IdProviders getIdProviders()
    {
        final NodeQuery query = NodeQuery.create()
            .parent( IdProviderNodeTranslator.ID_PROVIDERS_PARENT_PATH )
            .size( NodeQuery.ALL_RESULTS_SIZE_FLAG )
            .build();
        final Nodes nodes = callWithContext( () -> this.nodeService.getByIds( this.nodeService.findByQuery( query ).getNodeIds() ) );

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
        requireAdministratorToChangeMembers( from );

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
        requireAdministratorToChangeMembers( relationship.getFrom() );

        callWithContext( () -> {
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.removeRelationshipToUpdateNodeParams( relationship );
            nodeService.update( updateNodeParams );

            securityAuditLogSupport.removeRelationship( relationship );

            return null;
        } );
    }

    public void removeRelationships( final PrincipalKey from )
    {
        if ( RoleKeys.ADMIN.equals( from ) )
        {
            throw new IllegalArgumentException( "Super user cannot be removed from the administrator role" );
        }
        requireAdministratorToChangeMembers( from );

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
                .principals( authToken.getRestriction().apply( PrincipalKeys.from( RoleKeys.ADMIN ) ) )
                .principals( RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE )
                .restriction( authToken.getRestriction() )
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
                case VerifiedUserKeyAuthToken authToken ->
                {
                    final Node node = this.nodeService.getByPath( authToken.getUserKey().toPath() );
                    final User user = node == null ? null : PrincipalNodeTranslator.userFromNode( node );
                    return user != null && !user.isDisabled()
                        ? createAuthInfo( user, token.getRestriction() )
                        : AuthenticationInfo.unAuthenticated();
                }
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
                return createAuthInfo( user, token.getRestriction() );
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

    private AuthenticationInfo createAuthInfo( final User user, final RoleRestriction restriction )
    {
        final PrincipalKeys memberships = resolveMemberships( user.getKey() );
        return AuthenticationInfo.create()
            .principals( restriction.apply( memberships ) )
            .principals( RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE )
            .restriction( restriction )
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
        requireModifiable( key );
        requireAdministratorToModify( key );

        return callWithContext( () -> {
            final Node node = callWithContext( () -> this.nodeService.getByPath( key.toPath() ) );
            if ( node == null )
            {
                throw new NodeNotFoundException( "setPassword failed, user with key " + key + " not found" );
            }

            final String authenticationHash =
                password != null ? this.passwordSecurityService.defaultEncoder().encode( password.toCharArray() ) : null;

            final Node updatedNode = nodeService.update(
                PrincipalNodeTranslator.toUpdateNodeParams( key, PrincipalNodeTranslator::userFromNode,
                                                            user -> User.create( user ).authenticationHash( authenticationHash ).build() ) );

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
            .disabled( createUser.isDisabled() )
            .modifiedTime( Millis.now() )
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

    @Override
    public User createUser( final CreateUserParams createUser )
    {
        if ( createUser.getEmail() == null )
        {
            return doCreateUser( createUser );
        }
        return withLock( idProviderLocks, createUser.getKey().getIdProviderKey().toString(), () -> {
            duplicateEmailValidation( createUser.getKey(), createUser.getEmail() );
            return doCreateUser( createUser );
        } );
    }

    @Override
    public User updateUser( final UpdateUserParams updateUserParams )
    {
        if ( updateUserParams.getEmail() == null && updateUserParams.getEditor() == null )
        {
            return doUpdateUser( updateUserParams );
        }
        return withLock( idProviderLocks, updateUserParams.getKey().getIdProviderKey().toString(), () -> doUpdateUser( updateUserParams ) );
    }

    private static void requireModifiable( final PrincipalKey key )
    {
        if ( NON_MODIFIABLE_USER_PATHS.contains( key.toPath() ) )
        {
            throw new IllegalArgumentException( String.format( "[%s] user cannot be modified", key ) );
        }
    }

    private static boolean sameExceptProfile( final User current, final User changed )
    {
        return current.getKey().equals( changed.getKey() ) && Objects.equals( current.getDisplayName(), changed.getDisplayName() ) &&
            Objects.equals( current.getEmail(), changed.getEmail() ) && Objects.equals( current.getLogin(), changed.getLogin() ) &&
            current.isDisabled() == changed.isDisabled();
    }

    private User doUpdateUser( final UpdateUserParams updateUserParams )
    {
        return callWithContext( () -> {
            final PrincipalKey key = updateUserParams.getKey();
            if ( this.nodeService.getByPath( key.toPath() ) == null )
            {
                throw new PrincipalNotFoundException( key );
            }
            requireAdministratorToModify( key );

            final Node updatedNode =
                nodeService.update( PrincipalNodeTranslator.toUpdateNodeParams( key, PrincipalNodeTranslator::userFromNode, current -> {
                    final User changed = updateUserParams.update( current );
                    if ( !sameExceptProfile( current, changed ) )
                    {
                        requireModifiable( key );
                    }
                    if ( changed.getEmail() != null && !changed.getEmail().equals( current.getEmail() ) )
                    {
                        duplicateEmailValidation( key, changed.getEmail() );
                    }
                    return changed;
                } ) );
            final User updatedUser = PrincipalNodeTranslator.userFromNode( updatedNode );

            securityAuditLogSupport.updateUser( UpdateUserParams.create( updatedUser ).build() );

            return updatedUser;
        } );
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
            .modifiedTime( Millis.now() )
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

            final Node updatedNode = nodeService.update(
                PrincipalNodeTranslator.toUpdateNodeParams( updateGroupParams.getKey(), PrincipalNodeTranslator::groupFromNode,
                                                            updateGroupParams::update ) );
            final Group updatedGroup = PrincipalNodeTranslator.groupFromNode( updatedNode );

            securityAuditLogSupport.updateGroup( UpdateGroupParams.create( updatedGroup ).build() );

            return updatedGroup;
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
        final String roleId = createRole.getKey().getId().toLowerCase( Locale.ROOT );
        if ( RESERVED_ROLE_PREFIXES.stream().anyMatch( roleId::startsWith ) && !isCallerAdministrator() )
        {
            throw new IllegalArgumentException( String.format( "[%s] role is in a reserved namespace", createRole.getKey() ) );
        }

        final Role role = Role.create()
            .key( createRole.getKey() )
            .displayName( createRole.getDisplayName() )
            .modifiedTime( Millis.now() )
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

            final Node updatedNode = nodeService.update(
                PrincipalNodeTranslator.toUpdateNodeParams( updateRoleParams.getKey(), PrincipalNodeTranslator::roleFromNode,
                                                            updateRoleParams::update ) );
            final Role updatedRole = PrincipalNodeTranslator.roleFromNode( updatedNode );

            securityAuditLogSupport.updateRole( UpdateRoleParams.create( updatedRole ).build() );

            return updatedRole;
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
        return switch ( requireNonNull( principalKey, "Principal key was null" ).getType() )
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
        if ( IdProviderKey.system().toString().equalsIgnoreCase( idProviderKey.toString() ) )
        {
            throw new IllegalArgumentException( String.format( "[%s] id provider cannot be removed", idProviderKey ) );
        }
        if ( !isCallerAdministrator() && holdsAdministrators( idProviderKey ) )
        {
            throw new IllegalArgumentException(
                String.format( "[%s] id provider holds administrators and can only be removed by an administrator", idProviderKey ) );
        }

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
        if ( NON_REMOVABLE_PRINCIPAL_PATHS.contains( principalKey.toPath() ) )
        {
            throw new IllegalArgumentException( String.format( "[%s] principal cannot be removed", principalKey ) );
        }
        if ( principalKey.isRole() && principalKey.getId().toLowerCase( Locale.ROOT ).startsWith( PROJECT_ROLE_PREFIX ) &&
            !isCallerAdministrator() )
        {
            throw new IllegalArgumentException( String.format( "[%s] project role is removed with its project", principalKey ) );
        }
        requireAdministratorToModify( principalKey );

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
        if ( !ID_PROVIDER_KEY_PATTERN.matcher( createIdProviderParams.getKey().toString() ).matches() )
        {
            throw new IllegalArgumentException(
                String.format( "Invalid IdProviderKey [%s]: only letters A-Z and a-z, digits, '_' and '-' are allowed",
                               createIdProviderParams.getKey() ) );
        }

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

                final Node rootNode = nodeService.getById( NodeId.ROOT );
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

            final Node idProviderNode = nodeService.update( IdProviderNodeTranslator.toUpdateNodeParams( node.id(), current -> {
                final IdProvider changed = updateIdProviderParams.update( current );
                requireSystemIdProviderBound( current, changed );
                return changed;
            } ) );
            final IdProvider updatedIdProvider = IdProviderNodeTranslator.fromNode( idProviderNode );

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

                final Node rootNode = nodeService.getById( NodeId.ROOT );
                idProviderNodePermissions = mergeWithRootPermissions( idProviderNodePermissions, rootNode.getPermissions() );
                usersNodePermissions = mergeWithRootPermissions( usersNodePermissions, rootNode.getPermissions() );
                groupsNodePermissions = mergeWithRootPermissions( groupsNodePermissions, rootNode.getPermissions() );

                setNodePermissions( idProviderNode.id(), idProviderNodePermissions );
                setNodePermissions( usersNode.id(), usersNodePermissions );
                setNodePermissions( groupsNode.id(), groupsNodePermissions );
                scopes.setPermissions( updateIdProviderParams.getKey(), idProviderNodePermissions );
                clients.setPermissions( updateIdProviderParams.getKey(), idProviderNodePermissions );
            }

            securityAuditLogSupport.updateIdProvider( UpdateIdProviderParams.create( updatedIdProvider )
                                                          .permissions( updateIdProviderParams.getIdProviderPermissions() )
                                                          .build() );

            return updatedIdProvider;
        } );
    }

    private void setNodePermissions( final NodeId nodeId, final AccessControlList permissions )
    {
        nodeService.applyPermissions( ApplyNodePermissionsParams.create().nodeId( nodeId ).permissions( permissions ).build() );
        nodeService.refresh( RefreshMode.ALL );
    }

    @Override
    public Scope createScope( final CreateScopeParams params )
    {
        final Scope scope = scopes.create( params );
        securityAuditLogSupport.createScope( params );
        return scope;
    }

    @Override
    public Optional<Scope> updateScope( final UpdateScopeParams params )
    {
        final Optional<Scope> scope = scopes.update( params );
        scope.ifPresent( s -> securityAuditLogSupport.updateScope( params ) );
        return scope;
    }

    @Override
    public Optional<Scope> getScope( final IdProviderKey idProvider, final String id )
    {
        return scopes.get( idProvider, id );
    }

    @Override
    public Scopes getScopes( final IdProviderKey idProvider )
    {
        return scopes.list( idProvider );
    }

    @Override
    public boolean deleteScope( final IdProviderKey idProvider, final String id )
    {
        final boolean deleted = scopes.delete( idProvider, id );
        if ( deleted )
        {
            securityAuditLogSupport.removeScope( idProvider, id );
        }
        return deleted;
    }

    @Override
    public Client createClient( final CreateClientParams params )
    {
        final Client client = clients.create( params );
        securityAuditLogSupport.createClient( params );
        return client;
    }

    @Override
    public Optional<Client> updateClient( final UpdateClientParams params )
    {
        final Optional<Client> client = clients.update( params );
        client.ifPresent( c -> securityAuditLogSupport.updateClient( params ) );
        return client;
    }

    @Override
    public Optional<Client> getClient( final IdProviderKey idProvider, final String id )
    {
        return clients.get( idProvider, id );
    }

    @Override
    public Clients getClients( final IdProviderKey idProvider )
    {
        return clients.list( idProvider );
    }

    @Override
    public boolean deleteClient( final IdProviderKey idProvider, final String id )
    {
        if ( IdProviderKey.system().equals( idProvider ) && SecurityInitializer.ENONIC_CLI.equals( id ) )
        {
            throw new IllegalArgumentException( String.format( "[%s] client of [%s] cannot be removed", id, idProvider ) );
        }
        final boolean deleted = clients.delete( idProvider, id );
        if ( deleted )
        {
            securityAuditLogSupport.removeClient( idProvider, id );
        }
        return deleted;
    }

    private static void requireSystemIdProviderBound( final IdProvider current, final IdProvider changed )
    {
        if ( !IdProviderKey.system().equals( current.getKey() ) )
        {
            return;
        }
        if ( changed.getIdProviderConfig() == null )
        {
            throw new IllegalArgumentException(
                String.format( "[%s] id provider cannot be unbound from its application", current.getKey() ) );
        }
        final IdProviderConfig currentConfig = current.getIdProviderConfig();
        if ( ( currentConfig == null || !currentConfig.getApplicationKey().equals( changed.getIdProviderConfig().getApplicationKey() ) ) &&
            !isCallerAdministrator() )
        {
            throw new IllegalArgumentException(
                String.format( "[%s] id provider can only be bound to another application by an administrator", current.getKey() ) );
        }
    }

    private static boolean isCallerAdministrator()
    {
        return ContextAccessor.current().getAuthInfo().hasRole( RoleKeys.ADMIN );
    }

    /**
     * Only an administrator may change who holds the administrator role, directly or through a group or role that holds it.
     */
    private void requireAdministratorToChangeMembers( final PrincipalKey from )
    {
        if ( !isCallerAdministrator() && holdsAdministratorRole( from ) )
        {
            throw new IllegalArgumentException( String.format( "Members of [%s] can only be changed by an administrator", from ) );
        }
    }

    /**
     * Only an administrator may modify or remove a principal that holds the administrator role, directly or through a group or role.
     * Otherwise anyone able to write principals could take over an administrator account: by its password, its login or email,
     * or by a credential an id provider keeps in its profile.
     */
    private void requireAdministratorToModify( final PrincipalKey key )
    {
        if ( !isCallerAdministrator() && holdsAdministratorRole( key ) )
        {
            throw new IllegalArgumentException( String.format( "[%s] can only be modified by an administrator", key ) );
        }
    }

    private boolean holdsAdministratorRole( final PrincipalKey key )
    {
        return RoleKeys.ADMIN.toPath().equals( key.toPath() ) ||
            callAsAdministrator( () -> resolveMemberships( key ) ).contains( RoleKeys.ADMIN );
    }

    private boolean holdsAdministrators( final IdProviderKey idProviderKey )
    {
        return callAsAdministrator( () -> {
            final Set<PrincipalKey> visited = new HashSet<>();
            final Deque<PrincipalKey> containers = new ArrayDeque<>( List.of( RoleKeys.ADMIN ) );
            while ( !containers.isEmpty() )
            {
                for ( PrincipalRelationship relationship : getRelationships( containers.pop() ) )
                {
                    final PrincipalKey member = relationship.getTo();
                    if ( !member.isRole() && idProviderKey.toString().equalsIgnoreCase( member.getIdProviderKey().toString() ) )
                    {
                        return true;
                    }
                    if ( !member.isUser() && visited.add( member ) )
                    {
                        containers.push( member );
                    }
                }
            }
            return false;
        } );
    }

    /**
     * The guards above must see every membership, whatever the caller may read; they only read.
     */
    private <T> T callAsAdministrator( final Callable<T> callable )
    {
        final AuthenticationInfo authInfo =
            AuthenticationInfo.copyOf( ContextAccessor.current().getAuthInfo() ).principals( RoleKeys.ADMIN ).build();
        return ContextBuilder.from( ContextAccessor.current() ).authInfo( authInfo ).build().callWith( callable );
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
