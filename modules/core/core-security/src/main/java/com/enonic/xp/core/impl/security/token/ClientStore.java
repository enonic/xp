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
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.IdProviderNotFoundException;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.token.Client;
import com.enonic.xp.security.token.Clients;
import com.enonic.xp.security.token.ClientAlreadyExistsException;
import com.enonic.xp.security.token.CreateClientParams;
import com.enonic.xp.security.token.UpdateClientParams;

/**
 * The clients of an id provider as nodes under it (see {@link ClientNodes}), behind the security
 * service. Reads run with administrator rights, since a client's record is public; writes run as
 * the caller, so the user store's permissions decide who may change it.
 */
@NullMarked
public final class ClientStore
{
    private static final Logger LOG = LoggerFactory.getLogger( ClientStore.class );

    private final NodeService nodeService;

    public ClientStore( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    public Client create( final CreateClientParams params )
    {
        final Client client = params.getClient();
        final NodePath path = ClientNodes.clientPath( client.getIdProvider(), client.getId() );
        return asCaller( () -> {
            if ( !nodeService.nodeExists( ScopeNodes.idProviderPath( client.getIdProvider() ) ) )
            {
                throw new IdProviderNotFoundException( client.getIdProvider() );
            }
            createFolder( ClientNodes.clientsPath( client.getIdProvider() ) );
            try
            {
                final Node node = nodeService.create( CreateNodeParams.create()
                                                          .parent( path.getParentPath() )
                                                          .name( path.getName().toString() )
                                                          .nodeType( ClientNodes.CLIENT_TYPE )
                                                          .inheritPermissions( true )
                                                          .indexConfigDocument( ClientNodes.NO_INDEX )
                                                          .data( ClientNodes.toData( client ) )
                                                          .refresh( RefreshMode.ALL )
                                                          .build() );
                return ClientNodes.fromNode( client.getIdProvider(), node );
            }
            catch ( NodeAlreadyExistAtPathException e )
            {
                throw new ClientAlreadyExistsException( client.getIdProvider(), client.getId() );
            }
        } );
    }

    public Optional<Client> update( final UpdateClientParams params )
    {
        final NodePath path = ClientNodes.clientPath( params.getIdProvider(), params.getId() );
        return asCaller( () -> {
            final Node node = nodeService.getByPath( path );
            if ( node == null || !ClientNodes.isClient( node, params.getId() ) )
            {
                return Optional.empty();
            }
            final Node written = nodeService.update( UpdateNodeParams.create()
                                                         .id( node.id() )
                                                         .editor( editable -> editable.data = ClientNodes.toData(
                                                             params.applyTo( ClientNodes.fromData( params.getIdProvider(), params.getId(), editable.data ) ) ) )
                                                         .refresh( RefreshMode.ALL )
                                                         .build() );
            return Optional.of( ClientNodes.fromNode( params.getIdProvider(), written ) );
        } );
    }

    public Optional<Client> get( final IdProviderKey idProvider, final String id )
    {
        final NodePath path = ClientNodes.clientPath( idProvider, id );
        return asAdmin( () -> Optional.ofNullable( nodeService.getByPath( path ) )
            .filter( node -> ClientNodes.isClient( node, id ) )
            .map( node -> ClientNodes.fromNode( idProvider, node ) ) );
    }

    public Clients list( final IdProviderKey idProvider )
    {
        final NodePath clients = ClientNodes.clientsPath( idProvider );
        return asAdmin( () -> {
            if ( !nodeService.nodeExists( clients ) )
            {
                return Clients.empty();
            }
            final NodeIds ids = nodeService.list( ListNodesParams.create().parentPath( clients ).build() )
                .filter( entry -> clients.equals( entry.nodePath().getParentPath() ) )
                .map( NodeListEntry::nodeId )
                .collect( NodeIds.collector() );
            return nodeService.getByIds( ids )
                .stream()
                .filter( node -> ClientNodes.CLIENT_TYPE.equals( node.getNodeType() ) )
                .map( node -> ClientNodes.fromNode( idProvider, node ) )
                .sorted( Comparator.comparing( Client::getId ) )
                .collect( Clients.collector() );
        } );
    }

    public boolean delete( final IdProviderKey idProvider, final String id )
    {
        final NodePath path = ClientNodes.clientPath( idProvider, id );
        return asCaller( () -> {
            final Node node = nodeService.getByPath( path );
            if ( node == null || !ClientNodes.isClient( node, id ) )
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
     * Gives the clients, and their folder, the permissions of the id provider; nothing when the id
     * provider has no clients yet. The folder takes the permissions at creation, so a change to who
     * administers the id provider has to be passed on.
     */
    public void setPermissions( final IdProviderKey idProvider, final AccessControlList permissions )
    {
        final Node folder = nodeService.getByPath( ClientNodes.clientsPath( idProvider ) );
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
                                    .nodeType( ClientNodes.CLIENTS_TYPE )
                                    .inheritPermissions( true )
                                    .indexConfigDocument( ClientNodes.NO_INDEX )
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
