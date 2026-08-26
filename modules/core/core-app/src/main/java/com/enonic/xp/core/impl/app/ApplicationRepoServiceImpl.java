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
import com.enonic.xp.node.MoveNodeParams;
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

    // the new schema is staged here before replacing the cms node; the resolvers never address this name
    static final String CMS_STAGING_NAME = "cms_staging";

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
     * The new schema is built in full under a staging node invisible to the resolvers (they address {@code cms} only), and only then
     * swapped in: a failure while building leaves the previously persisted schema untouched and served. The swap itself is delete+rename;
     * a crash in between leaves no {@code cms} node, with the complete new schema still under staging — reinstalling repairs it.
     */
    @Override
    public void persistApplicationSchema( final ApplicationKey applicationKey, final Map<String, ByteSource> resources )
    {
        ApplicationHelper.runAsAdmin( () -> {
            final NodePath appPath = new NodePath( APPLICATION_PATH, NodeName.from( applicationKey.getName() ) );
            final NodePath cmsPath = new NodePath( appPath, NodeName.from( VirtualAppConstants.CMS_ROOT_NAME ) );
            final NodePath stagingPath = new NodePath( appPath, NodeName.from( CMS_STAGING_NAME ) );

            // leftover of an earlier failed install
            if ( this.nodeService.nodeExists( stagingPath ) )
            {
                this.nodeService.delete( DeleteNodeParams.create().nodePath( stagingPath ).refresh( RefreshMode.ALL ).build() );
            }

            final Node stagingNode = this.nodeService.create( CreateNodeParams.create()
                                                                  .parent( appPath )
                                                                  .name( CMS_STAGING_NAME )
                                                                  .inheritPermissions( true )
                                                                  .refresh( RefreshMode.ALL )
                                                                  .build() );

            try
            {
                resources.forEach( ( path, content ) -> createResourceNode( stagingPath, path, content ) );
            }
            catch ( RuntimeException e )
            {
                try
                {
                    this.nodeService.delete( DeleteNodeParams.create().nodePath( stagingPath ).refresh( RefreshMode.ALL ).build() );
                }
                catch ( Exception cleanupFailure )
                {
                    e.addSuppressed( cleanupFailure );
                }
                throw e;
            }

            if ( this.nodeService.nodeExists( cmsPath ) )
            {
                this.nodeService.delete( DeleteNodeParams.create().nodePath( cmsPath ).refresh( RefreshMode.ALL ).build() );
            }

            this.nodeService.move( MoveNodeParams.create()
                                       .nodeId( stagingNode.id() )
                                       .newName( NodeName.from( VirtualAppConstants.CMS_ROOT_NAME ) )
                                       .refresh( RefreshMode.ALL )
                                       .build() );

            this.nodeService.refresh( RefreshMode.ALL );
        } );
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
