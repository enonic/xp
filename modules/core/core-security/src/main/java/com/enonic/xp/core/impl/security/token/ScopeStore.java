package com.enonic.xp.core.impl.security.token;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.node.ApplyPermissionsScope;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderNotFoundException;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.token.CreateScopeParams;
import com.enonic.xp.security.token.Scope;
import com.enonic.xp.security.token.Scopes;
import com.enonic.xp.security.token.ScopeAlreadyExistsException;
import com.enonic.xp.security.token.UpdateScopeParams;

/**
 * The scopes of an id provider as nodes under it (see {@link ScopeNodes}), behind the security
 * service. Reads run with administrator rights, since the scopes are public; writes run as the
 * caller, so the user store's permissions decide who may change it.
 */
@NullMarked
public final class ScopeStore
{
    private static final Logger LOG = LoggerFactory.getLogger( ScopeStore.class );

    private final NodeService nodeService;

    public ScopeStore( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    public Scope create( final CreateScopeParams params )
    {
        final Scope scope = params.getScope();
        final NodePath path = ScopeNodes.scopePath( scope.getIdProvider(), scope.getId() );
        return asCaller( () -> {
            if ( !nodeService.nodeExists( ScopeNodes.idProviderPath( scope.getIdProvider() ) ) )
            {
                throw new IdProviderNotFoundException( scope.getIdProvider() );
            }
            createFolder( ScopeNodes.scopesPath( scope.getIdProvider() ) );
            try
            {
                final Node node = nodeService.create( CreateNodeParams.create()
                                                          .parent( path.getParentPath() )
                                                          .name( path.getName().toString() )
                                                          .nodeType( ScopeNodes.SCOPE_TYPE )
                                                          .inheritPermissions( true )
                                                          .indexConfigDocument( ScopeNodes.NO_INDEX )
                                                          .data( ScopeNodes.toData( scope ) )
                                                          .refresh( RefreshMode.ALL )
                                                          .build() );
                return ScopeNodes.fromNode( scope.getIdProvider(), node );
            }
            catch ( NodeAlreadyExistAtPathException e )
            {
                throw new ScopeAlreadyExistsException( scope.getIdProvider(), scope.getId() );
            }
        } );
    }

    public Optional<Scope> update( final UpdateScopeParams params )
    {
        final NodePath path = ScopeNodes.scopePath( params.getIdProvider(), params.getId() );
        return asCaller( () -> {
            final Node node = nodeService.getByPath( path );
            if ( node == null || !ScopeNodes.SCOPE_TYPE.equals( node.getNodeType() ) )
            {
                return Optional.empty();
            }
            final Node written = nodeService.update( UpdateNodeParams.create()
                                                         .id( node.id() )
                                                         .editor( editable -> editable.data = ScopeNodes.toData(
                                                             params.applyTo( ScopeNodes.fromData( params.getIdProvider(), params.getId(), editable.data ) ) ) )
                                                         .refresh( RefreshMode.ALL )
                                                         .build() );
            return Optional.of( ScopeNodes.fromNode( params.getIdProvider(), written ) );
        } );
    }

    public Optional<Scope> get( final IdProviderKey idProvider, final String id )
    {
        final NodePath path;
        try
        {
            path = ScopeNodes.scopePath( idProvider, id );
        }
        catch ( IllegalArgumentException e )
        {
            return Optional.empty();
        }
        return asAdmin( () -> Optional.ofNullable( nodeService.getByPath( path ) )
            .filter( node -> ScopeNodes.SCOPE_TYPE.equals( node.getNodeType() ) )
            .map( node -> ScopeNodes.fromNode( idProvider, node ) ) );
    }

    public Scopes list( final IdProviderKey idProvider )
    {
        final NodePath scopes = ScopeNodes.scopesPath( idProvider );
        return asAdmin( () -> {
            if ( !nodeService.nodeExists( scopes ) )
            {
                return Scopes.empty();
            }
            return nodeService.getByIds( nodeService.findByQuery( NodeQuery.create().parent( scopes ).size( NodeQuery.ALL_RESULTS_SIZE_FLAG ).build() )
                                             .getNodeIds() )
                .stream()
                .filter( node -> ScopeNodes.SCOPE_TYPE.equals( node.getNodeType() ) )
                .map( node -> ScopeNodes.fromNode( idProvider, node ) )
                .sorted( Comparator.comparing( Scope::getId ) )
                .collect( Scopes.collector() );
        } );
    }

    public boolean delete( final IdProviderKey idProvider, final String id )
    {
        final NodePath path;
        try
        {
            path = ScopeNodes.scopePath( idProvider, id );
        }
        catch ( IllegalArgumentException e )
        {
            return false;
        }
        return asCaller( () -> {
            final Node node = nodeService.getByPath( path );
            if ( node == null || !ScopeNodes.SCOPE_TYPE.equals( node.getNodeType() ) )
            {
                return false;
            }
            try
            {
                nodeService.delete( DeleteNodeParams.create().nodeId( node.id() ).refresh( RefreshMode.ALL ).build() );
                return true;
            }
            catch ( NodeNotFoundException e )
            {
                return false;
            }
        } );
    }

    /**
     * Gives the scopes, and their folder, the permissions of the id provider; nothing when the id
     * provider has no scopes yet. The folder takes the permissions at creation, so a change to who
     * administers the id provider has to be passed on.
     */
    public void setPermissions( final IdProviderKey idProvider, final AccessControlList permissions )
    {
        final Node folder = nodeService.getByPath( ScopeNodes.scopesPath( idProvider ) );
        if ( folder != null )
        {
            nodeService.applyPermissions( ApplyNodePermissionsParams.create()
                                              .nodeId( folder.id() )
                                              .permissions( permissions )
                                              .scope( ApplyPermissionsScope.TREE )
                                              .build() );
            nodeService.refresh( RefreshMode.ALL );
        }
    }

    private void createFolder( final NodePath folder )
    {
        if ( nodeService.nodeExists( folder ) )
        {
            return;
        }
        try
        {
            nodeService.create( CreateNodeParams.create()
                                    .parent( folder.getParentPath() )
                                    .name( folder.getName().toString() )
                                    .nodeType( ScopeNodes.SCOPES_TYPE )
                                    .inheritPermissions( true )
                                    .indexConfigDocument( ScopeNodes.NO_INDEX )
                                    .data( new PropertyTree() )
                                    .refresh( RefreshMode.ALL )
                                    .build() );
        }
        catch ( NodeAlreadyExistAtPathException e )
        {
            LOG.debug( "Folder [{}] already created by another writer", folder );
        }
    }

    private <T> T asCaller( final Callable<T> body )
    {
        return securityRepo( ContextAccessor.current().getAuthInfo() ).callWith( body );
    }

    private <T> T asAdmin( final Callable<T> body )
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.copyOf( ContextAccessor.current().getAuthInfo() ).principals( RoleKeys.ADMIN ).build();
        return securityRepo( authInfo ).callWith( body );
    }

    private static Context securityRepo( final AuthenticationInfo authInfo )
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SecurityConstants.BRANCH_SECURITY )
            .authInfo( authInfo )
            .build();
    }
}
