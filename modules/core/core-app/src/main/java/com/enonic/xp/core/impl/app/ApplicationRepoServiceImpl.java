package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationDescriptor;
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
import com.enonic.xp.core.internal.Millis;
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
    public Node createApplicationNode( final ApplicationKey applicationKey )
    {
        return ApplicationHelper.runAsAdmin( () -> {
            final PropertyTree data = new PropertyTree();
            data.setInstant( ApplicationPropertyNames.MODIFIED_TIME, Millis.now() );
            return this.nodeService.create( CreateNodeParams.create()
                                                .parent( APPLICATION_PATH )
                                                .name( applicationKey.getName() )
                                                .data( data )
                                                .inheritPermissions( true )
                                                .refresh( RefreshMode.ALL )
                                                .build() );
        } );
    }

    /**
     * Node of an application in system-repo, the parent of its persisted schema ({@code cms}).
     */
    static NodePath applicationNodePath( final ApplicationKey applicationKey )
    {
        return new NodePath( APPLICATION_PATH, NodeName.from( applicationKey.getName() ) );
    }

    /**
     * Node of the persisted application descriptor ({@code enonic.yaml}), the icon is attached to it.
     */
    static NodePath applicationDescriptorNodePath( final ApplicationKey applicationKey )
    {
        return new NodePath( applicationNodePath( applicationKey ), NodeName.from( SchemaResourcePaths.APP_DESCRIPTOR_NAME ) );
    }

    @Override
    public void deleteApplicationNode( final ApplicationKey applicationKey )
    {
        this.nodeService.delete( DeleteNodeParams.create()
                                           .nodePath( applicationNodePath( applicationKey ) )
                                           .refresh( RefreshMode.ALL )
                                           .build() );
    }

    /**
     * The previously persisted schema is replaced by the new one. A failure while writing leaves no schema at all
     * (the persisted nodes are removed again), never a mix of old and new: reinstalling a fixed version repairs it.
     * The {@code cms} node exists once a cms schema is persisted, it marks the application as node backed; the descriptor node
     * ({@code enonic.yaml}) exists once the application ships a descriptor or an icon, whether or not it owns a schema.
     */
    @Override
    public void persistApplicationSchema( final ApplicationKey applicationKey, final Map<String, ByteSource> resources )
    {
        ApplicationHelper.runAsAdmin( () -> {
            final NodePath appPath = applicationNodePath( applicationKey );

            deletePersistedSchema( appPath );

            try
            {
                final Map<String, ByteSource> schemaResources = new LinkedHashMap<>( resources );
                final ByteSource descriptor = schemaResources.remove( SchemaResourcePaths.APP_DESCRIPTOR_NAME );
                final ByteSource icon = schemaResources.remove( SchemaResourcePaths.APP_ICON_NAME );

                if ( descriptor != null || icon != null )
                {
                    // an icon without a descriptor still needs the descriptor node to be attached to
                    final String yaml = descriptor != null
                        ? readString( descriptor )
                        : YmlApplicationDescriptorSerializer.serialize( ApplicationDescriptor.create().key( applicationKey ).build() );
                    final ApplicationIconUpdate iconUpdate =
                        icon != null ? ApplicationIconUpdate.replace( icon, SchemaResourcePaths.SVG_MIME_TYPE ) : ApplicationIconUpdate.KEEP;
                    createDescriptorNode( appPath, yaml, iconUpdate );
                }

                // the persisted tree was just removed: the folders created on the way to the resources are tracked here,
                // the cms folder is created as the marker of a persisted schema as soon as the application ships one
                final Set<NodePath> folders = new HashSet<>();
                if ( schemaResources.keySet().stream().anyMatch( path -> path.startsWith( SchemaResourceNames.CMS_ROOT_NAME + "/" ) ) )
                {
                    folders.add( createFolderNode( appPath, SchemaResourceNames.CMS_ROOT_NAME ) );
                }

                schemaResources.forEach( ( path, content ) -> createResourceNode( appPath, path, content, folders ) );
            }
            catch ( RuntimeException e )
            {
                try
                {
                    deletePersistedSchema( appPath );
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
        ApplicationHelper.runAsAdmin( () -> deletePersistedSchema( applicationNodePath( applicationKey ) ) );
    }

    @Override
    public Node getApplicationDescriptorNode( final ApplicationKey applicationKey )
    {
        return ApplicationHelper.runAsAdmin( () -> this.nodeService.getByPath( applicationDescriptorNodePath( applicationKey ) ) );
    }

    @Override
    public Node upsertApplicationDescriptor( final ApplicationKey applicationKey, final String descriptor,
                                             final ApplicationIconUpdate iconUpdate )
    {
        return ApplicationHelper.runAsAdmin( () -> {
            final NodePath descriptorPath = applicationDescriptorNodePath( applicationKey );

            final Node node;
            if ( this.nodeService.nodeExists( descriptorPath ) )
            {
                final UpdateNodeParams.Builder params = UpdateNodeParams.create().path( descriptorPath ).editor( toBeEdited -> {
                    toBeEdited.data.setString( SchemaNodePropertyNames.RESOURCE, descriptor );
                    switch ( iconUpdate )
                    {
                        case ApplicationIconUpdate.Keep keep ->
                        {
                        }
                        case ApplicationIconUpdate.Remove remove ->
                        {
                            toBeEdited.data.removeProperties( SchemaNodePropertyNames.MIME_TYPE );
                            toBeEdited.data.removeProperties( SchemaNodePropertyNames.ICON );
                        }
                        case ApplicationIconUpdate.Replace replace -> setIconProperties( toBeEdited.data, replace.mimeType() );
                    }
                } ).refresh( RefreshMode.ALL );

                if ( iconUpdate instanceof ApplicationIconUpdate.Replace replace )
                {
                    params.attachBinary( SchemaResourceNames.APP_ICON_BINARY_REFERENCE, replace.data() );
                }
                node = this.nodeService.update( params.build() );
            }
            else
            {
                node = createDescriptorNode( applicationNodePath( applicationKey ), descriptor, iconUpdate );
            }

            this.nodeService.refresh( RefreshMode.ALL );
            return node;
        } );
    }

    private Node createDescriptorNode( final NodePath appPath, final String descriptor, final ApplicationIconUpdate iconUpdate )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( SchemaNodePropertyNames.RESOURCE, descriptor );

        final CreateNodeParams.Builder params = CreateNodeParams.create()
            .name( SchemaResourcePaths.APP_DESCRIPTOR_NAME )
            .parent( appPath )
            .inheritPermissions( true )
            .refresh( RefreshMode.ALL );

        if ( iconUpdate instanceof ApplicationIconUpdate.Replace replace )
        {
            setIconProperties( data, replace.mimeType() );
            params.attachBinary( SchemaResourceNames.APP_ICON_BINARY_REFERENCE, replace.data() );
        }

        return this.nodeService.create( params.data( data ).build() );
    }

    private static void setIconProperties( final PropertyTree data, final String mimeType )
    {
        data.setString( SchemaNodePropertyNames.MIME_TYPE, mimeType );
        data.setBinaryReference( SchemaNodePropertyNames.ICON, SchemaResourceNames.APP_ICON_BINARY_REFERENCE );
    }

    private void deletePersistedSchema( final NodePath appPath )
    {
        for ( final String name : SchemaResourcePaths.PERSISTED_ROOT_NAMES )
        {
            deleteIfExists( new NodePath( appPath, NodeName.from( name ) ) );
        }
    }

    private void deleteIfExists( final NodePath nodePath )
    {
        if ( this.nodeService.nodeExists( nodePath ) )
        {
            this.nodeService.delete( DeleteNodeParams.create().nodePath( nodePath ).refresh( RefreshMode.ALL ).build() );
        }
    }

    private NodePath createFolderNode( final NodePath parent, final String name )
    {
        this.nodeService.create( CreateNodeParams.create()
                                     .name( name )
                                     .parent( parent )
                                     .inheritPermissions( true )
                                     .refresh( RefreshMode.ALL )
                                     .build() );
        return new NodePath( parent, NodeName.from( name ) );
    }

    private void createResourceNode( final NodePath appPath, final String resourcePath, final ByteSource content,
                                     final Set<NodePath> folders )
    {
        final String[] elements = resourcePath.split( "/" );

        NodePath parent = appPath;
        for ( int i = 0; i < elements.length - 1; i++ )
        {
            final NodePath folderPath = new NodePath( parent, NodeName.from( elements[i] ) );
            if ( folders.add( folderPath ) )
            {
                createFolderNode( parent, elements[i] );
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
            data.setBinaryReference( SchemaNodePropertyNames.ICON, SchemaResourceNames.ICON_BINARY_REFERENCE );
            params.attachBinary( SchemaResourceNames.ICON_BINARY_REFERENCE, content );
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
