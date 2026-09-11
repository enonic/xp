package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.schema.SchemaNodePropertyNames;
import com.enonic.xp.util.BinaryReference;

public class ApplicationRepoServiceImpl
    implements ApplicationRepoService
{
    static final NodePath APPLICATION_PATH = new NodePath( NodePath.ROOT, NodeName.from( "applications" ) );

    private final NodeService nodeService;

    public ApplicationRepoServiceImpl( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    @Override
    public Node upsertApplicationNode( final AppInfo application, final ByteSource source )
    {
        if ( doGetNodeByName( application.name ) != null )
        {
            return this.nodeService.update( ApplicationNodeTransformer.toUpdateNodeParams( application, source ) );
        }
        else
        {
            return this.nodeService.create( ApplicationNodeTransformer.toCreateNodeParams( application, source ) );
        }
    }

    @Override
    public void deleteApplicationNode( final ApplicationKey applicationKey )
    {
        this.nodeService.delete( DeleteNodeParams.create()
                                           .nodePath( new NodePath( APPLICATION_PATH, NodeName.from( applicationKey.getName() ) ) )
                                           .refresh( RefreshMode.ALL )
                                           .build() );
    }

    /**
     * The previously persisted schema is replaced by the new one. A failure while writing leaves no schema at all
     * (the {@code cms} node is removed again), never a mix of old and new: reinstalling a fixed version repairs it.
     */
    @Override
    public void persistApplicationSchema( final ApplicationKey applicationKey, final Map<String, ByteSource> resources )
    {
        ApplicationHelper.runAsAdmin( () -> {
            final NodePath appPath = new NodePath( APPLICATION_PATH, NodeName.from( applicationKey.getName() ) );
            final NodePath cmsPath = new NodePath( appPath, NodeName.from( VirtualAppConstants.CMS_ROOT_NAME ) );

            deleteIfExists( cmsPath );

            try
            {
                this.nodeService.create( CreateNodeParams.create()
                                             .parent( appPath )
                                             .name( VirtualAppConstants.CMS_ROOT_NAME )
                                             .inheritPermissions( true )
                                             .refresh( RefreshMode.ALL )
                                             .build() );

                resources.forEach( ( path, content ) -> createResourceNode( cmsPath, path, content ) );
            }
            catch ( RuntimeException e )
            {
                try
                {
                    deleteIfExists( cmsPath );
                }
                catch ( Exception cleanupFailure )
                {
                    e.addSuppressed( cleanupFailure );
                }
                throw e;
            }

            this.nodeService.refresh( RefreshMode.ALL );
        } );
    }

    @Override
    public void deleteApplicationSchema( final ApplicationKey applicationKey )
    {
        ApplicationHelper.runAsAdmin( () -> deleteIfExists(
            new NodePath( new NodePath( APPLICATION_PATH, NodeName.from( applicationKey.getName() ) ),
                          NodeName.from( VirtualAppConstants.CMS_ROOT_NAME ) ) ) );
    }

    private void deleteIfExists( final NodePath nodePath )
    {
        if ( this.nodeService.nodeExists( nodePath ) )
        {
            this.nodeService.delete( DeleteNodeParams.create().nodePath( nodePath ).refresh( RefreshMode.ALL ).build() );
        }
    }

    private void createResourceNode( final NodePath cmsPath, final String resourcePath, final ByteSource content )
    {
        final String[] elements = resourcePath.split( "/" );

        NodePath parent = cmsPath;
        for ( int i = 0; i < elements.length - 1; i++ )
        {
            final NodePath folderPath = new NodePath( parent, NodeName.from( elements[i] ) );
            if ( !this.nodeService.nodeExists( folderPath ) )
            {
                this.nodeService.create( CreateNodeParams.create()
                                             .name( elements[i] )
                                             .parent( parent )
                                             .inheritPermissions( true )
                                             .refresh( RefreshMode.ALL )
                                             .build() );
            }
            parent = folderPath;
        }

        final String name = elements[elements.length - 1];
        final String iconMimeType = SchemaResourcePaths.iconMimeType( name );

        final CreateNodeParams.Builder params = CreateNodeParams.create()
            .name( name )
            .parent( parent )
            .inheritPermissions( true )
            .refresh( RefreshMode.ALL );

        final PropertyTree data = new PropertyTree();

        if ( iconMimeType != null )
        {
            // icons are stored as node binaries, the descriptors and phrases as a text property
            data.setString( SchemaNodePropertyNames.MIME_TYPE, iconMimeType );
            data.setBinaryReference( SchemaNodePropertyNames.ICON, VirtualAppConstants.ICON_BINARY_REFERENCE );
            params.attachBinary( VirtualAppConstants.ICON_BINARY_REFERENCE, content );
        }
        else
        {
            data.setString( SchemaNodePropertyNames.RESOURCE, readString( content ) );
        }

        this.nodeService.create( params.data( data ).build() );
    }

    private static String readString( final ByteSource content )
    {
        try
        {
            return content.asCharSource( StandardCharsets.UTF_8 ).read();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public ByteSource getApplicationSource( final NodeId nodeId )
    {
        return this.nodeService.getBinary( nodeId, BinaryReference.from( ApplicationNodeTransformer.APPLICATION_BINARY_REF ) );
    }

    @Override
    public Node getApplicationNode( final ApplicationKey applicationKey )
    {
        return doGetNodeByName( applicationKey.getName() );
    }

    @Override
    public Nodes getApplications()
    {
        final NodeIds applicationIds = ApplicationHelper.runAsAdmin(
            () -> this.nodeService.list( ListNodesParams.create().parentPath( APPLICATION_PATH ).build() )
                .filter( entry -> APPLICATION_PATH.equals( entry.nodePath().getParentPath() ) )
                .map( NodeListEntry::nodeId )
                .collect( NodeIds.collector() ) );

        return this.nodeService.getByIds( applicationIds );
    }

    @Override
    public Node updateStartedState( final ApplicationKey appKey, final boolean started )
    {
        final Node applicationNode = doGetNodeByName( appKey.getName() );

        if ( applicationNode == null )
        {
            throw new NodeNotFoundException( "Didnt find application node in repo" );
        }

        return this.nodeService.update( UpdateNodeParams.create()
                                                         .id( applicationNode.id() )
                                                         .editor(
                                                             toBeEdited -> toBeEdited.data.setBoolean( ApplicationPropertyNames.STARTED,
                                                                                                       started ) )
                                                          .refresh( RefreshMode.ALL )
                                                         .build() );
    }

    private Node doGetNodeByName( final String applicationName )
    {
        return this.nodeService.getByPath( new NodePath( APPLICATION_PATH, NodeName.from( applicationName ) ) );
    }
}
