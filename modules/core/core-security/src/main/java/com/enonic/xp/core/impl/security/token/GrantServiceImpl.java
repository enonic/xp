package com.enonic.xp.core.impl.security.token;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.core.internal.UuidHelper;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.token.CreateGrantParams;
import com.enonic.xp.security.token.Grant;
import com.enonic.xp.security.token.GrantService;
import com.enonic.xp.security.token.GrantWithRefreshToken;
import com.enonic.xp.security.token.RedeemRefreshTokenParams;
import com.enonic.xp.security.token.UpdateGrantParams;
import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;

import static com.enonic.xp.core.impl.security.SharedLocks.withLock;
import static com.enonic.xp.core.impl.security.token.GrantNodes.GrantNode;

/**
 * Reads the {@code token.grant.*} settings as one snapshot, so a rejected configuration changes
 * nothing.
 */
@Component(configurationPid = "com.enonic.xp.security")
public class GrantServiceImpl
    implements GrantService
{
    static final String USER_LOCKS = "com.enonic.xp.security.token.grants.lock.user";

    static final String GRANT_LOCKS = "com.enonic.xp.security.token.grants.lock.grant";

    static final String GRACE_SECRET_MAP = "com.enonic.xp.security.token.grants.grace";

    static final Duration DEFAULT_TTL = Duration.ofDays( 90 );

    static final Duration DEFAULT_ROTATE_AFTER = Duration.ofMinutes( 5 );

    static final Duration DEFAULT_REUSE_GRACE = Duration.ofSeconds( 30 );

    static final int DEFAULT_MAX_PER_USER = 100;

    static final int DEFAULT_MAX_PER_CLIENT = 20;

    private static final Logger LOG = LoggerFactory.getLogger( GrantServiceImpl.class );

    private final NodeService nodeService;

    private final SharedMap<String, Boolean> userLocks;

    private final SharedMap<String, Boolean> grantLocks;

    private final TokenSigningKeys graceSecret;

    private final Clock clock;

    private volatile Settings settings;

    @Activate
    public GrantServiceImpl( @Reference final NodeService nodeService, @Reference final SharedMapService sharedMapService )
    {
        this( nodeService, sharedMapService, Clock.systemUTC() );
    }

    GrantServiceImpl( final NodeService nodeService, final SharedMapService sharedMapService, final Clock clock )
    {
        this.nodeService = nodeService;
        this.userLocks = sharedMapService.getSharedMap( USER_LOCKS );
        this.grantLocks = sharedMapService.getSharedMap( GRANT_LOCKS );
        this.graceSecret = new SharedMapTokenSigningKeys( sharedMapService, GRACE_SECRET_MAP,
                                                          "Generated secret [{}] for refresh token rotation grace; it lives as long as the cluster" );
        this.clock = clock;
    }

    /**
     * Reads the {@code token.grant.*} settings and takes them into use together, so a rejected
     * configuration changes nothing.
     */
    @Activate
    @Modified
    public void activate( final Map<String, ?> properties )
    {
        final Map<String, String> config = ConfigBuilder.create().addAll( properties ).build().asMap();
        this.settings = new Settings( duration( config, "token.grant.ttl", DEFAULT_TTL ),
                                      duration( config, "token.grant.rotateAfter", DEFAULT_ROTATE_AFTER ),
                                      duration( config, "token.grant.reuseGrace", DEFAULT_REUSE_GRACE ),
                                      positiveInt( config, "token.grant.maxPerUser", DEFAULT_MAX_PER_USER ),
                                      positiveInt( config, "token.grant.maxPerClient", DEFAULT_MAX_PER_CLIENT ) );
    }

    /**
     * The current time in whole seconds, the precision of a grant's timestamps and of its tokens' claims.
     */
    private Instant now()
    {
        return clock.instant().truncatedTo( ChronoUnit.SECONDS );
    }

    @Override
    public GrantWithRefreshToken create( final CreateGrantParams params )
    {
        final Settings settings = this.settings;
        final PrincipalKey user = params.getSubject();
        final byte[] key = RefreshTokens.newGrantKey();
        final GrantWithRefreshToken created = withLock( userLocks, user.toString(), () -> inSecurityRepo( () -> {
            final NodePath grants = grantsFolder( user ).orElseThrow( () -> new IllegalArgumentException( "User not found: " + user ) );
            final List<GrantNode> existing = new ArrayList<>( children( grants ) );
            final Instant now = now();
            final String id = UuidHelper.newUUIDv7( now.toEpochMilli() ).toString();
            final String refreshToken = RefreshTokens.newToken( key, id, 0 );
            final GrantNode grant =
                new GrantNode( id, user, params.getClientId(), params.getAudience(), params.getScope(), params.getRestriction(),
                               now, now, now.plus( params.getTtl() != null ? params.getTtl() : settings.ttl() ),
                               RefreshTokens.hash( refreshToken ), key, 0, null );
            delete( existing, g -> !g.isLive( now ) );
            evictOldest( existing, g -> g.clientId().equals( grant.clientId() ), settings.maxPerClient() - 1 );
            evictOldest( existing, g -> true, settings.maxPerUser() - 1 );
            final Node node = nodeService.create( CreateNodeParams.create()
                                                      .setNodeId( NodeId.from( id ) )
                                                      .parent( grants )
                                                      .name( id )
                                                      .nodeType( GrantNodes.GRANT_TYPE )
                                                      .inheritPermissions( true )
                                                      .indexConfigDocument( GrantNodes.NO_INDEX )
                                                      .data( grant.toData() )
                                                      .refresh( RefreshMode.ALL )
                                                      .build() );
            return new GrantWithRefreshToken( GrantNode.from( node ).toGrant(), refreshToken );
        } ) );
        return created;
    }

    @Override
    public Optional<GrantWithRefreshToken> redeem( final RedeemRefreshTokenParams params )
    {
        final Settings settings = this.settings;
        final String refreshToken = params.getRefreshToken();
        final Optional<RefreshTokens.Parts> parsed = RefreshTokens.parse( refreshToken );
        if ( parsed.isEmpty() )
        {
            return Optional.empty();
        }
        final RefreshTokens.Parts parts = parsed.get();
        final String presented = RefreshTokens.hash( refreshToken );
        return withLock( grantLocks, parts.grantId(), () -> inSecurityRepo( () -> {
            final Optional<GrantNode> found = grant( parts.grantId() );
            final Instant now = now();
            if ( found.isEmpty() || !found.get().isLive( now ) )
            {
                return Optional.<GrantWithRefreshToken>empty();
            }
            final GrantNode grant = found.get();
            if ( !meets( grant, params ) )
            {
                return Optional.<GrantWithRefreshToken>empty();
            }
            final GrantNodes.Grace grace = grant.grace();
            if ( presented.equals( grant.hash() ) )
            {
                if ( now.isBefore( grant.rotatedAt().plus( settings.rotateAfter() ) ) )
                {
                    if ( grace != null && !now.isBefore( grace.until() ) )
                    {
                        change( grant.id(), GrantNode::withoutGrace );
                    }
                    return Optional.of( new GrantWithRefreshToken( grant.toGrant(), refreshToken ) );
                }
                final String next = RefreshTokens.newToken( grant.key(), grant.id(), grant.generation() + 1 );
                final GrantNodes.Grace nextGrace = new GrantNodes.Grace( presented, now.plus( settings.reuseGrace() ),
                                                                         RefreshTokens.wrap( graceSecret(), refreshToken, next ) );
                final GrantNode rotated = change( grant.id(), g -> g.rotated( RefreshTokens.hash( next ), now, nextGrace ) );
                return Optional.of( new GrantWithRefreshToken( rotated.toGrant(), next ) );
            }
            if ( grace != null && presented.equals( grace.hash() ) && now.isBefore( grace.until() ) )
            {
                return RefreshTokens.unwrap( graceSecret(), refreshToken, grace.next() )
                    .map( next -> new GrantWithRefreshToken( grant.toGrant(), next ) );
            }
            if ( RefreshTokens.isIssuedUnder( parts, grant.key() ) && parts.generation() < grant.generation() )
            {
                LOG.warn( "Retired refresh token of grant [{}] for [{}] presented again: revoking the grant", grant.id(), grant.subject() );
                delete( grant );
            }
            return Optional.<GrantWithRefreshToken>empty();
        } ) );
    }

    /**
     * Whether the grant matches the redeem's conditions. Checked before anything is done with the
     * token, so a redeem at the wrong endpoint or by the wrong client leaves the grant untouched. The
     * client is compared as the string it named itself by.
     */
    private static boolean meets( final GrantNode grant, final RedeemRefreshTokenParams params )
    {
        final boolean idProviderMatches =
            params.getIdProvider() == null || params.getIdProvider().equals( grant.subject().getIdProviderKey() );
        return idProviderMatches && params.getClientId().equals( grant.clientId() );
    }

    private byte[] graceSecret()
    {
        return graceSecret.signingKey().secret();
    }

    @Override
    public List<Grant> list( final PrincipalKey user )
    {
        final List<GrantNode> grants = inSecurityRepo( () -> children( GrantNodes.grantsPath( user ) ) );
        final Instant now = now();
        return grants.stream()
            .filter( g -> g.isLive( now ) )
            .sorted( Comparator.comparing( GrantNode::createdAt ).reversed() )
            .map( GrantNode::toGrant )
            .toList();
    }

    @Override
    public Optional<Grant> get( final PrincipalKey user, final String grantId )
    {
        final Instant now = now();
        return inSecurityRepo( () -> grant( grantId ) ).filter( g -> g.subject().equals( user ) && g.isLive( now ) ).map( GrantNode::toGrant );
    }

    @Override
    public Optional<Grant> update( final UpdateGrantParams params )
    {
        return withLock( grantLocks, params.getGrantId(), () -> inSecurityRepo( () -> {
            final Instant now = now();
            final Optional<GrantNode> current =
                grant( params.getGrantId() ).filter( g -> g.subject().equals( params.getSubject() ) && g.isLive( now ) );
            if ( current.isEmpty() )
            {
                return Optional.empty();
            }
            final GrantNode changed = change( current.get().id(), g -> g.changed( params.getScope() != null ? params.getScope() : g.scope(),
                                                                                  params.getAudience() != null ? params.getAudience() : g.audience(),
                                                                                  params.getRestriction() != null
                                                                                      ? params.getRestriction()
                                                                                      : g.restriction() ) );
            return Optional.of( changed.toGrant() );
        } ) );
    }

    @Override
    public boolean revoke( final PrincipalKey user, final String grantId )
    {
        return withLock( grantLocks, grantId, () -> inSecurityRepo( () -> {
            final Optional<GrantNode> grant = grant( grantId ).filter( g -> g.subject().equals( user ) );
            grant.ifPresent( this::delete );
            return grant.isPresent();
        } ) );
    }

    @Override
    public void revokeAll( final PrincipalKey user )
    {
        withLock( userLocks, user.toString(), () -> inSecurityRepo( () -> {
            final NodePath authTokens = GrantNodes.authTokensPath( user );
            if ( !nodeService.nodeExists( authTokens ) )
            {
                return null;
            }
            for ( GrantNode grant : children( GrantNodes.grantsPath( user ) ) )
            {
                withLock( grantLocks, grant.id(), () -> {
                    delete( grant );
                    return null;
                } );
            }
            nodeService.delete( DeleteNodeParams.create().nodePath( authTokens ).refresh( RefreshMode.ALL ).build() );
            return null;
        } ) );
    }

    /**
     * The user's grants folder, created on first use; empty when the user does not exist.
     */
    private Optional<NodePath> grantsFolder( final PrincipalKey user )
    {
        final NodePath grants = GrantNodes.grantsPath( user );
        if ( nodeService.nodeExists( grants ) )
        {
            return Optional.of( grants );
        }
        if ( !nodeService.nodeExists( user.toPath() ) )
        {
            return Optional.empty();
        }
        createFolder( user.toPath(), GrantNodes.AUTH_TOKENS, GrantNodes.AUTH_TOKENS_TYPE );
        createFolder( GrantNodes.authTokensPath( user ), GrantNodes.GRANTS, GrantNodes.GRANTS_TYPE );
        return Optional.of( grants );
    }

    private void createFolder( final NodePath parent, final String name, final NodeType type )
    {
        try
        {
            nodeService.create( CreateNodeParams.create()
                                    .parent( parent )
                                    .name( name )
                                    .nodeType( type )
                                    .inheritPermissions( true )
                                    .indexConfigDocument( GrantNodes.NO_INDEX )
                                    .data( new PropertyTree() )
                                    .refresh( RefreshMode.ALL )
                                    .build() );
        }
        catch ( NodeAlreadyExistAtPathException e )
        {
            LOG.debug( "Node [{}] already created by another writer", new NodePath( parent, NodeName.from( name ) ) );
        }
    }

    /**
     * The grant with the id, if it sits in its subject's grants folder.
     */
    private Optional<GrantNode> grant( final String grantId )
    {
        final Node node;
        try
        {
            node = nodeService.getById( NodeId.from( grantId ) );
        }
        catch ( NodeNotFoundException | IllegalArgumentException e )
        {
            return Optional.empty();
        }
        if ( !GrantNodes.GRANT_TYPE.equals( node.getNodeType() ) )
        {
            return Optional.empty();
        }
        final GrantNode grant = GrantNode.from( node );
        final boolean owned = node.parentPath().equals( GrantNodes.grantsPath( grant.subject() ) );
        return owned ? Optional.of( grant ) : Optional.empty();
    }

    private List<GrantNode> children( final NodePath grants )
    {
        return nodeService.getByIds( nodeService.findByParent( FindNodesByParentParams.create().parentPath( grants ).build() ).getNodeIds() )
            .stream()
            .filter( node -> GrantNodes.GRANT_TYPE.equals( node.getNodeType() ) )
            .map( GrantNode::from )
            .toList();
    }

    private GrantNode change( final String grantId, final UnaryOperator<GrantNode> change )
    {
        final Node node = nodeService.update( UpdateNodeParams.create()
                                                  .id( NodeId.from( grantId ) )
                                                  .editor( editable -> editable.data = change.apply( GrantNode.from( editable.build() ) ).toData() )
                                                  .build() );
        return GrantNode.from( node );
    }

    private void delete( final GrantNode grant )
    {
        try
        {
            nodeService.delete( DeleteNodeParams.create().nodeId( NodeId.from( grant.id() ) ).refresh( RefreshMode.ALL ).build() );
        }
        catch ( NodeNotFoundException e )
        {
            LOG.debug( "Grant [{}] already deleted", grant.id() );
        }
    }

    private void delete( final List<GrantNode> grants, final Predicate<GrantNode> which )
    {
        for ( GrantNode grant : List.copyOf( grants ) )
        {
            if ( which.test( grant ) )
            {
                withLock( grantLocks, grant.id(), () -> {
                    delete( grant );
                    return null;
                } );
                grants.remove( grant );
            }
        }
    }

    private void evictOldest( final List<GrantNode> grants, final Predicate<GrantNode> among, final int keep )
    {
        final List<GrantNode> candidates = grants.stream().filter( among ).sorted( Comparator.comparing( GrantNode::createdAt ) ).toList();
        delete( grants, candidates.subList( 0, Math.max( 0, candidates.size() - keep ) )::contains );
    }

    private <T> T inSecurityRepo( final Supplier<T> body )
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authInfo = AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.ADMIN ).build();
        return ContextBuilder.from( context )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SecurityConstants.BRANCH_SECURITY )
            .authInfo( authInfo )
            .build()
            .callWith( body::get );
    }

    private static Duration duration( final Map<String, String> config, final String key, final Duration defaultValue )
    {
        final String value = config.get( key );
        final Duration duration = value == null || value.isBlank() ? defaultValue : Duration.parse( value.trim() );
        if ( duration.isNegative() || duration.isZero() )
        {
            throw new IllegalArgumentException( key + " must be positive: " + duration );
        }
        return duration;
    }

    private static int positiveInt( final Map<String, String> config, final String key, final int defaultValue )
    {
        final String value = config.get( key );
        final int number = value == null || value.isBlank() ? defaultValue : Integer.parseInt( value.trim() );
        if ( number < 1 )
        {
            throw new IllegalArgumentException( key + " must be positive: " + number );
        }
        return number;
    }

    private record Settings(Duration ttl, Duration rotateAfter, Duration reuseGrace, int maxPerUser, int maxPerClient)
    {
    }
}
