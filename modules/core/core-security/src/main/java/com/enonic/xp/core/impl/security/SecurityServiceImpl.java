package com.enonic.xp.core.impl.security;

import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.Striped;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
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
import com.enonic.xp.security.PrincipalType;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UpdateGroupParams;
import com.enonic.xp.security.UpdateIdProviderParams;
import com.enonic.xp.security.UpdateRoleParams;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserQuery;
import com.enonic.xp.security.UserQueryResult;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.IdProviderAccessControlList;
import com.enonic.xp.security.auth.AuthenticationException;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.security.auth.EmailPasswordAuthToken;
import com.enonic.xp.security.auth.UsernamePasswordAuthToken;
import com.enonic.xp.security.auth.VerifiedEmailAuthToken;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;

import static com.enonic.xp.core.impl.security.SecurityInitializer.DEFAULT_ID_PROVIDER_ACL;
import static com.google.common.base.Strings.isNullOrEmpty;

@Component(immediate = true)
public final class SecurityServiceImpl
    implements SecurityService
{
    private static final Logger LOG = LoggerFactory.getLogger( SecurityServiceImpl.class );

    private static final ImmutableSet<PrincipalKey> FORBIDDEN_FROM_RELATIONSHIP =
        ImmutableSet.of( RoleKeys.EVERYONE, RoleKeys.AUTHENTICATED );

    private static final String SU_PASSWORD_PROPERTY_KEY = "xp.suPassword";

    private static final Pattern SU_PASSWORD_PATTERN =
        Pattern.compile( "(?:\\{(sha1|sha256|sha512|md5)\\})?(\\S+)", Pattern.CASE_INSENSITIVE );

    private static final long INITIALIZATION_CHECK_PERIOD = 1000;

    private static final long INITIALIZATION_CHECK_MAX_COUNT = 30;

    private final Clock clock;

    private final PasswordEncoder passwordEncoder = new PBKDF2Encoder();

    private final SecureRandom secureRandom = new SecureRandom();

    private final Striped<Lock> userEmailLocks = Striped.lazyWeakLock( 100 );

    private NodeService nodeService;

    private IndexService indexService;

    private String suPasswordHashing;

    private String suPasswordValue;

    public SecurityServiceImpl()
    {
        this.clock = Clock.systemUTC();
    }

    @Activate
    public void initialize()
    {
        initializeSuPassword();
        SecurityInitializer.create().
            setIndexService( indexService ).
            setSecurityService( this ).
            setNodeService( nodeService ).
            build().
            initialize();
    }

    private void initializeSuPassword()
    {
        final String suPasswordPropertyValue = Strings.emptyToNull( System.getProperty( SU_PASSWORD_PROPERTY_KEY ) );
        if ( suPasswordPropertyValue != null )
        {
            final Matcher suPasswordMatcher = SU_PASSWORD_PATTERN.matcher( suPasswordPropertyValue );
            if ( suPasswordMatcher.find() )
            {
                this.suPasswordHashing = suPasswordMatcher.group( 1 );
                this.suPasswordHashing = this.suPasswordHashing == null ? null : this.suPasswordHashing.toLowerCase();
                this.suPasswordValue = suPasswordMatcher.group( 2 );
            }
        }
    }

    @Override
    public IdProviders getIdProviders()
    {
        final FindNodesByParentParams findByParent = FindNodesByParentParams.create().
            parentPath( IdProviderNodeTranslator.getIdProvidersParentPath() ).build();
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
    public IdProviderAccessControlList getDefaultIdProviderPermissions()
    {
        return DEFAULT_ID_PROVIDER_ACL;
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
            throw new IllegalArgumentException( "Invalid 'from' value in relationship: " + from.toString() );
        }
        callWithContext( () -> {
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.addRelationshipToUpdateNodeParams( relationship );
            nodeService.update( updateNodeParams );

            this.nodeService.refresh( RefreshMode.SEARCH );

            return null;
        } );
    }

    @Override
    public void removeRelationship( final PrincipalRelationship relationship )
    {
        callWithContext( () -> {
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.removeRelationshipToUpdateNodeParams( relationship );
            nodeService.update( updateNodeParams );

            this.nodeService.refresh( RefreshMode.SEARCH );

            return null;
        } );
    }

    @Override
    public void removeRelationships( final PrincipalKey from )
    {
        callWithContext( () -> {
            doRemoveRelationships( from );
            this.nodeService.refresh( RefreshMode.SEARCH );
            return null;
        } );
    }

    private void doRemoveRelationships( final PrincipalKey from )
    {
        final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.removeAllRelationshipsToUpdateNodeParams( from );
        nodeService.update( updateNodeParams );
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

    private void removeRelationships( final IdProviderKey from )
    {
        callWithContext( () -> {
            final NodePath idProviderNodePath = IdProviderNodeTranslator.toIdProviderNodePath( from );
            final Node node = this.nodeService.getByPath( idProviderNodePath );
            final UpdateNodeParams updateNodeParams = IdProviderNodeTranslator.removeAllRelationshipsToUpdateNodeParams( node );
            nodeService.update( updateNodeParams );

            this.nodeService.refresh( RefreshMode.SEARCH );

            return null;
        } );
    }

    private PrincipalKeys resolveMemberships( final PrincipalKey userKey )
    {
        final Set<PrincipalKey> resolvedMemberships = new LinkedHashSet<>();
        final PrincipalKeys directMemberships = queryDirectMemberships( userKey );
        resolvedMemberships.addAll( directMemberships.getSet() );

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
                final FindNodesByQueryResult result = this.nodeService.findByQuery( NodeQuery.create().
                    addQueryFilter( ValueFilter.create().
                        fieldName( PrincipalPropertyNames.MEMBER_KEY ).
                        addValue( ValueFactory.newString( member.toString() ) ).
                        build() ).
                    size( NodeQuery.ALL_RESULTS_SIZE_FLAG ).
                    build() );
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
    @Deprecated
    public Principals findPrincipals( final IdProviderKey idProvider, final List<PrincipalType> types, final String query )
    {
        final PrincipalQuery.Builder principalQuery = PrincipalQuery.create().
            getAll().
            includeTypes( types ).
            searchText( query );
        if ( idProvider != null )
        {
            principalQuery.idProvider( idProvider );
        }

        final PrincipalQueryResult result = query( principalQuery.build() );
        return result.getPrincipals();
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
        if ( !( token instanceof VerifiedUsernameAuthToken ) && !( token instanceof VerifiedEmailAuthToken ) )
        {
            addRandomDelay();
        }

        if ( isSuAuthenticationEnabled( token ) )
        {
            return authenticateSu( (UsernamePasswordAuthToken) token );
        }

        if ( token.getIdProvider() != null )
        {
            return doAuthenticate( token );
        }
        else
        {
            final IdProviders idProviders = callAsAuthenticated( this::getIdProviders );
            for ( IdProvider idProvider : idProviders )
            {
                token.setIdProvider( idProvider.getKey() );
                final AuthenticationInfo authInfo = doAuthenticate( token );
                if ( authInfo.isAuthenticated() )
                {
                    return authInfo;
                }
            }
            return AuthenticationInfo.unAuthenticated();
        }
    }

    private boolean isSuAuthenticationEnabled( final AuthenticationToken token )
    {
        if ( this.suPasswordValue != null && token instanceof UsernamePasswordAuthToken )
        {
            UsernamePasswordAuthToken usernamePasswordAuthToken = (UsernamePasswordAuthToken) token;
            return (usernamePasswordAuthToken.getIdProvider() == null ||
                IdProviderKey.system().equals( usernamePasswordAuthToken.getIdProvider() )) &&
                SecurityInitializer.SUPER_USER.getId().equals( usernamePasswordAuthToken.getUsername() );
        }
        return false;
    }

    private AuthenticationInfo authenticateSu( final UsernamePasswordAuthToken token )
    {
        final String hashedTokenPassword = hashSuPassword( token.getPassword() );
        if ( this.suPasswordValue.equals( hashedTokenPassword ) )
        {
            final User admin = User.create().
                key( SecurityInitializer.SUPER_USER ).
                login( SecurityInitializer.SUPER_USER.getId() ).
                displayName( "Super User" ).
                build();
            return AuthenticationInfo.create().
                principals( RoleKeys.ADMIN, RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE ).
                user( admin ).
                build();
        }
        else
        {
            return AuthenticationInfo.unAuthenticated();
        }
    }

    private String hashSuPassword( final String plainPassword )
    {
        if ( this.suPasswordHashing == null )
        {
            return plainPassword;
        }

        final HashFunction hashFunction;
        switch ( this.suPasswordHashing )
        {
            case "sha1":
                hashFunction = Hashing.sha1();
                break;
            case "sha256":
                hashFunction = Hashing.sha256();
                break;
            case "sha512":
                hashFunction = Hashing.sha512();
                break;
            case "md5":
                hashFunction = Hashing.md5();
                break;
            default:
                throw new IllegalArgumentException( "Incorrect type of encryption: " + this.suPasswordHashing );
        }

        return hashFunction.newHasher().
            putString( plainPassword, Charset.defaultCharset() ).
            hash().
            toString();
    }

    private void addRandomDelay()
    {
        try
        {
            Thread.sleep( secureRandom.nextInt( 130 ) + 20 );
        }
        catch ( InterruptedException e )
        {
            // Thread interrupted during sleep, nothing to do
        }
    }

    private AuthenticationInfo doAuthenticate( final AuthenticationToken token )
    {
        return callAsAuthenticated( () -> {
            if ( token instanceof UsernamePasswordAuthToken )
            {
                return authenticateUsernamePassword( (UsernamePasswordAuthToken) token );
            }
            else if ( token instanceof EmailPasswordAuthToken )
            {
                return authenticateEmailPassword( (EmailPasswordAuthToken) token );
            }
            else if ( token instanceof VerifiedUsernameAuthToken )
            {
                return authenticateVerifiedUsername( (VerifiedUsernameAuthToken) token );
            }
            else if ( token instanceof VerifiedEmailAuthToken )
            {
                return authenticateVerifiedEmail( (VerifiedEmailAuthToken) token );
            }
            else
            {
                throw new AuthenticationException( "Authentication token not supported: " + token.getClass().getSimpleName() );
            }
        } );
    }

    private AuthenticationInfo authenticateEmailPassword( final EmailPasswordAuthToken token )
    {
        final User user = findByEmail( token.getIdProvider(), token.getEmail() );
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
        final User user = findByUsername( token.getIdProvider(), token.getUsername() );
        if ( user != null && !user.isDisabled() && passwordMatch( user, token.getPassword() ) )
        {
            return createAuthInfo( user );
        }
        else
        {
            return AuthenticationInfo.unAuthenticated();
        }
    }

    private AuthenticationInfo authenticateVerifiedEmail( final VerifiedEmailAuthToken token )
    {
        final User user = findByEmail( token.getIdProvider(), token.getEmail() );
        if ( user != null && !user.isDisabled() )
        {
            return createAuthInfo( user );
        }
        else
        {
            return AuthenticationInfo.unAuthenticated();
        }
    }

    private AuthenticationInfo authenticateVerifiedUsername( final VerifiedUsernameAuthToken token )
    {
        final User user = findByUsername( token.getIdProvider(), token.getUsername() );
        if ( user != null && !user.isDisabled() )
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
        if ( isNullOrEmpty( password ) )
        {
            return false;
        }

        return this.passwordEncoder.validate( password, user.getAuthenticationHash() );
    }

    private User findByUsername( final IdProviderKey idProvider, final String username )
    {
        final CompareExpr idProviderExpr =
            CompareExpr.create( FieldExpr.from( PrincipalIndexPath.ID_PROVIDER_KEY ), CompareExpr.Operator.EQ,
                                ValueExpr.string( idProvider.toString() ) );
        final CompareExpr userNameExpr =
            CompareExpr.create( FieldExpr.from( PrincipalIndexPath.LOGIN_KEY ), CompareExpr.Operator.EQ, ValueExpr.string( username ) );
        final QueryExpr query = QueryExpr.from( LogicalExpr.and( idProviderExpr, userNameExpr ) );
        final Nodes nodes = callWithContext( () -> {
            final FindNodesByQueryResult result = nodeService.findByQuery( NodeQuery.create().query( query ).build() );
            return this.nodeService.getByIds( result.getNodeIds() );
        } );

        if ( nodes.getSize() > 1 )
        {
            throw new IllegalArgumentException( "Expected at most 1 user with username " + username + " in id provider " + idProvider );
        }

        return nodes.isEmpty() ? null : PrincipalNodeTranslator.userFromNode( nodes.first() );
    }

    private User findByEmail( final IdProviderKey idProvider, final String email )
    {
        final CompareExpr idProviderExpr =
            CompareExpr.create( FieldExpr.from( PrincipalIndexPath.ID_PROVIDER_KEY ), CompareExpr.Operator.EQ,
                                ValueExpr.string( idProvider.toString() ) );
        final CompareExpr userNameExpr =
            CompareExpr.create( FieldExpr.from( PrincipalIndexPath.EMAIL_KEY ), CompareExpr.Operator.EQ, ValueExpr.string( email ) );
        final QueryExpr query = QueryExpr.from( LogicalExpr.and( idProviderExpr, userNameExpr ) );
        final Nodes nodes = callWithContext( () -> {
            final FindNodesByQueryResult result = nodeService.findByQuery( NodeQuery.create().query( query ).build() );
            return this.nodeService.getByIds( result.getNodeIds() );
        } );

        if ( nodes.getSize() > 1 )
        {
            throw new IllegalArgumentException( "Expected at most 1 user with email " + email + " in id provider " + idProvider );
        }

        return nodes.isEmpty() ? null : PrincipalNodeTranslator.userFromNode( nodes.first() );
    }

    @Override
    public User setPassword( final PrincipalKey key, final String password )
    {
        Preconditions.checkArgument( key.isUser(), "Expected principal key of type User" );
        Preconditions.checkArgument( password != null && password.length() > 0, "Password cannot be empty" );

        return callWithContext( () -> {

            final Node node = callWithContext( () -> this.nodeService.getByPath( key.toPath() ) );
            if ( node == null )
            {
                throw new NodeNotFoundException( "setPassword failed, user with key " + key + " not found" );
            }

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

    private User doCreateUser( final CreateUserParams createUser )
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
            final Node node = callWithContext( () -> {
                final Node createdNode = nodeService.create( createNodeParams );
                this.nodeService.refresh( RefreshMode.SEARCH );
                return createdNode;
            } );

            if ( createUser.getPassword() != null )
            {
                return setPassword( user.getKey(), createUser.getPassword() );
            }

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

            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.toUpdateNodeParams( userToUpdate );

            final Node updatedNode = nodeService.update( updateNodeParams );

            this.nodeService.refresh( RefreshMode.SEARCH );

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
                "A user with email '" + email + "' already exists in id provider '" + key.getIdProviderKey().toString() + "'" );
        }
    }

    @Override
    public Optional<User> getUser( final PrincipalKey userKey )
    {
        Preconditions.checkArgument( userKey.isUser(), "Expected principal key of type User" );

        final Node node = callWithContext( () -> this.nodeService.getByPath( userKey.toPath() ) );
        return node == null ? Optional.empty() : Optional.ofNullable( PrincipalNodeTranslator.userFromNode( node ) );
    }

    @Override
    public Group createGroup( final CreateGroupParams createGroup )
    {
        final Group group = Group.create().
            key( createGroup.getKey() ).
            displayName( createGroup.getDisplayName() ).
            modifiedTime( Instant.now( clock ) ).
            description( createGroup.getDescription() ).
            build();

        final CreateNodeParams createGroupParams = PrincipalNodeTranslator.toCreateNodeParams( group );
        try
        {
            final Node node = callWithContext( () -> {
                final Node createdNode = this.nodeService.create( createGroupParams );
                this.nodeService.refresh( RefreshMode.SEARCH );
                return createdNode;
            } );

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
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.toUpdateNodeParams( groupToUpdate );

            final Node updatedNode = nodeService.update( updateNodeParams );

            this.nodeService.refresh( RefreshMode.SEARCH );

            return PrincipalNodeTranslator.groupFromNode( updatedNode );
        } );
    }

    @Override
    public Optional<Group> getGroup( final PrincipalKey groupKey )
    {
        Preconditions.checkArgument( groupKey.isGroup(), "Expected principal key of type Group" );

        final Node node = callWithContext( () -> this.nodeService.getByPath( groupKey.toPath() ) );
        return node == null ? Optional.empty() : Optional.ofNullable( PrincipalNodeTranslator.groupFromNode( node ) );
    }

    @Override
    public Role createRole( final CreateRoleParams createRole )
    {
        final Role role = Role.create().
            key( createRole.getKey() ).
            displayName( createRole.getDisplayName() ).
            modifiedTime( Instant.now( clock ) ).
            description( createRole.getDescription() ).
            build();

        final CreateNodeParams createNodeParams = PrincipalNodeTranslator.toCreateNodeParams( role );
        try
        {
            final Node node = callWithContext( () -> {
                final Node createdNode = this.nodeService.create( createNodeParams );
                this.nodeService.refresh( RefreshMode.SEARCH );
                return createdNode;
            } );

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
            final UpdateNodeParams updateNodeParams = PrincipalNodeTranslator.toUpdateNodeParams( roleToUpdate );

            final Node updatedNode = nodeService.update( updateNodeParams );

            this.nodeService.refresh( RefreshMode.SEARCH );

            return PrincipalNodeTranslator.roleFromNode( updatedNode );
        } );
    }

    @Override
    public Optional<Role> getRole( final PrincipalKey roleKey )
    {
        Preconditions.checkArgument( roleKey.isRole(), "Expected principal key of type Role" );

        final Node node = callWithContext( () -> this.nodeService.getByPath( roleKey.toPath() ) );
        return node == null ? Optional.empty() : Optional.ofNullable( PrincipalNodeTranslator.roleFromNode( node ) );
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
        removeRelationships( idProviderKey );
        final NodeIds deletedNodes = callWithContext( () -> {
            final NodePath idProviderNodePath = IdProviderNodeTranslator.toIdProviderNodePath( idProviderKey );
            final Node node = this.nodeService.getByPath( idProviderNodePath );
            if ( node == null )
            {
                return null;
            }
            return this.nodeService.deleteById( node.id() );
        } );
        if ( deletedNodes == null )
        {
            throw new IdProviderNotFoundException( idProviderKey );
        }
    }

    @Override
    public void deletePrincipal( final PrincipalKey principalKey )
    {
        final NodeIds deletedNodes = callWithContext( () -> {
            doRemoveRelationships( principalKey );
            doRemoveMemberships( principalKey );

            final NodeIds nodes = this.nodeService.deleteByPath( principalKey.toPath() );
            this.nodeService.refresh( RefreshMode.SEARCH );
            return nodes;
        } );
        if ( deletedNodes == null && deletedNodes.getSize() > 0 )
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
            final Nodes nodes = callWithContext( () -> this.nodeService.getByIds( result.getNodeIds() ) );

            final Principals principals = PrincipalNodeTranslator.fromNodes( nodes );
            return PrincipalQueryResult.create().
                addPrincipals( principals ).
                totalSize( Ints.checkedCast( result.getTotalHits() ) ).
                build();
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
            return UserQueryResult.create().
                addUsers( principals ).
                totalSize( Ints.checkedCast( result.getTotalHits() ) ).
                build();
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
            data.setSet( IdProviderPropertyNames.ID_PROVIDER_CONFIG_FORM_KEY, idProviderConfig.getConfig().getRoot() );
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

                final Node rootNode = nodeService.getRoot();
                idProviderNodePermissions = mergeWithRootPermissions( idProviderNodePermissions, rootNode.getPermissions() );
                usersNodePermissions = mergeWithRootPermissions( usersNodePermissions, rootNode.getPermissions() );
                groupsNodePermissions = mergeWithRootPermissions( groupsNodePermissions, rootNode.getPermissions() );

                final Node idProviderNode = nodeService.create( CreateNodeParams.create().
                    parent( IdProviderNodeTranslator.getIdProvidersParentPath() ).
                    name( createIdProviderParams.getKey().toString() ).
                    data( data ).
                    permissions( idProviderNodePermissions ).
                    build() );

                nodeService.create( CreateNodeParams.create().
                    parent( idProviderNode.path() ).
                    name( IdProviderNodeTranslator.USER_FOLDER_NODE_NAME ).
                    permissions( usersNodePermissions ).
                    build() );
                nodeService.create( CreateNodeParams.create().
                    parent( idProviderNode.path() ).
                    name( IdProviderNodeTranslator.GROUP_FOLDER_NODE_NAME ).
                    permissions( groupsNodePermissions ).
                    build() );

                final ApplyNodePermissionsParams applyPermissions = ApplyNodePermissionsParams.create().
                    nodeId( rootNode.id() ).
                    overwriteChildPermissions( false ).
                    build();
                nodeService.applyPermissions( applyPermissions );

                this.nodeService.refresh( RefreshMode.SEARCH );

                return idProviderNode;
            } );

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

                final Node rootNode = nodeService.getRoot();
                idProviderNodePermissions = mergeWithRootPermissions( idProviderNodePermissions, rootNode.getPermissions() );
                usersNodePermissions = mergeWithRootPermissions( usersNodePermissions, rootNode.getPermissions() );
                groupsNodePermissions = mergeWithRootPermissions( groupsNodePermissions, rootNode.getPermissions() );

                setNodePermissions( idProviderNode.id(), idProviderNodePermissions );
                setNodePermissions( usersNode.id(), usersNodePermissions );
                setNodePermissions( groupsNode.id(), groupsNodePermissions );

                final ApplyNodePermissionsParams applyPermissions = ApplyNodePermissionsParams.create().
                    nodeId( idProviderNode.id() ).
                    overwriteChildPermissions( false ).
                    build();
                nodeService.applyPermissions( applyPermissions );
            }

            this.nodeService.refresh( RefreshMode.SEARCH );

            return IdProviderNodeTranslator.fromNode( idProviderNode );
        } );
    }

    private void setNodePermissions( final NodeId nodeId, final AccessControlList permissions )
    {
        final UpdateNodeParams updateParams = UpdateNodeParams.create().
            id( nodeId ).
            editor( editableNode -> editableNode.permissions = permissions ).
            build();

        nodeService.update( updateParams );

        this.nodeService.refresh( RefreshMode.SEARCH );

    }

    private <T> T callWithContext( Callable<T> runnable )
    {
        return this.getContext().callWith( runnable );
    }

    private Context getContext()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return ContextBuilder.from( SecurityConstants.CONTEXT_SECURITY ).authInfo( authInfo ).build();
    }

    private <T> T callAsAuthenticated( Callable<T> runnable )
    {
        return this.getAuthenticatedContext().callWith( runnable );
    }

    private Context getAuthenticatedContext()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.AUTHENTICATED ).user( User.ANONYMOUS ).build();
        return ContextBuilder.from( SecurityConstants.CONTEXT_SECURITY ).authInfo( authInfo ).build();
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Reference
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
