package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.Millis;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.IconUpdate;
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
    // the applications are the children of the root of the application repository, see ApplicationRepoConstants
    static final NodePath APPLICATIONS_ROOT = NodePath.ROOT;

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
                                                .parent( APPLICATIONS_ROOT )
                                                .name( applicationKey.getName() )
                                                .data( data )
                                                .inheritPermissions( true )
                                                .refresh( RefreshMode.ALL )
                                                .build() );
        } );
    }

    /**
     * Node of an application in the application repository: it holds the application descriptor and icon, and is the parent of
     * the persisted schema ({@code cms}).
     */
    static NodePath applicationNodePath( final ApplicationKey applicationKey )
    {
        return new NodePath( APPLICATIONS_ROOT, NodeName.from( applicationKey.getName() ) );
    }

    /**
     * Root of the persisted schema ({@code cms}) below the application node.
     */
    static NodePath cmsNodePath( final ApplicationKey applicationKey )
    {
        return new NodePath( applicationNodePath( applicationKey ), SchemaNodeLayout.CMS_NODE_PATH.getName() );
    }

    /**
     * Node of the persisted cms descriptor ({@code cms/cms}), the marker of an application owning a schema.
     */
    static NodePath cmsDescriptorNodePath( final ApplicationKey applicationKey )
    {
        return schemaNodePath( applicationKey, SchemaNodeLayout.Kind.CMS.nodePath( null ) );
    }

    /**
     * The absolute path of a schema node located relative to the application node.
     */
    static NodePath schemaNodePath( final ApplicationKey applicationKey, final NodePath relativePath )
    {
        return new NodePath( applicationNodePath( applicationKey ) + relativePath.toString() );
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
     * The {@code cms} node exists once a cms schema is persisted, it marks the application as owning a schema; the descriptor is
     * persisted on the application node once the application ships a descriptor or an icon, whether or not it owns a schema.
     */
    @Override
    public void persistApplicationSchema( final ApplicationKey applicationKey, final Map<String, ByteSource> resources )
    {
        ApplicationHelper.runAsAdmin( () -> {
            final NodePath appPath = applicationNodePath( applicationKey );

            deletePersistedSchema( appPath );

            try
            {
                final Map<NodePath, PersistedNode> nodes = groupByNode( resources );

                final PersistedNode application = nodes.remove( NodePath.ROOT );
                if ( application != null )
                {
                    // an icon without a descriptor still needs the descriptor to be attached to
                    final String yaml = application.descriptor != null
                        ? readString( application.descriptor )
                        : YmlApplicationDescriptorSerializer.serialize( ApplicationDescriptor.create().key( applicationKey ).build() );
                    final IconUpdate iconUpdate =
                        application.icon != null ? IconUpdate.replace( application.icon, application.iconMimeType ) : IconUpdate.KEEP;
                    writeApplicationDescriptor( appPath, yaml, iconUpdate );
                }

                if ( !nodes.isEmpty() )
                {
                    // the cms node is the marker of an application owning a schema: created as soon as it ships one
                    this.nodeService.create( CreateNodeParams.create()
                                                 .name( SchemaNodeLayout.CMS_NODE_PATH.getName() )
                                                 .parent( appPath )
                                                 .inheritPermissions( true )
                                                 .refresh( RefreshMode.ALL )
                                                 .build() );
                    nodes.forEach( ( relativePath, node ) -> createSchemaNode( appPath, relativePath, node ) );
                }
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

    // a node as persisted: its descriptor and, for the kinds that have one, its icon
    private static final class PersistedNode
    {
        ByteSource descriptor;

        ByteSource icon;

        String iconMimeType;
    }

    /**
     * The resources by the node they are persisted in, the application node under {@link NodePath#ROOT}, in the order received.
     */
    private static Map<NodePath, PersistedNode> groupByNode( final Map<String, ByteSource> resources )
    {
        final Map<NodePath, PersistedNode> nodes = new LinkedHashMap<>();
        resources.forEach( ( path, content ) -> {
            final SchemaNodeLayout.Location location = SchemaNodeLayout.locate( path );
            if ( location == null )
            {
                throw new IllegalArgumentException( "Not a persisted application resource: " + path );
            }
            final PersistedNode node = nodes.computeIfAbsent( location.nodePath(), key -> new PersistedNode() );
            if ( location.icon() )
            {
                node.icon = content;
                node.iconMimeType = SchemaResourcePaths.iconMimeType( path );
            }
            else
            {
                node.descriptor = content;
            }
        } );
        return nodes;
    }

    /**
     * A schema node holds its descriptor as text and its icon as a binary named by its format. An icon without a descriptor is
     * not a schema and is dropped.
     */
    private void createSchemaNode( final NodePath appPath, final NodePath relativePath, final PersistedNode node )
    {
        if ( node.descriptor == null )
        {
            return;
        }
        final PropertyTree data = new PropertyTree();
        data.setString( SchemaNodePropertyNames.RESOURCE, readString( node.descriptor ) );

        final CreateNodeParams.Builder params = CreateNodeParams.create()
            .name( relativePath.getName() )
            .parent( new NodePath( appPath, SchemaNodeLayout.CMS_NODE_PATH.getName() ) )
            .inheritPermissions( true )
            .refresh( RefreshMode.ALL );

        if ( node.icon != null )
        {
            final BinaryReference reference = SchemaNodeLayout.iconReference( node.iconMimeType );
            data.setBinaryReference( SchemaNodePropertyNames.ICON, reference );
            params.attachBinary( reference, node.icon );
        }

        this.nodeService.create( params.data( data ).build() );
    }

    @Override
    public void deleteApplicationSchema( final ApplicationKey applicationKey )
    {
        ApplicationHelper.runAsAdmin( () -> deletePersistedSchema( applicationNodePath( applicationKey ) ) );
    }

    @Override
    public boolean hasApplicationDescriptor( final ApplicationKey applicationKey )
    {
        final Node node = ApplicationHelper.runAsAdmin( () -> this.nodeService.getByPath( applicationNodePath( applicationKey ) ) );
        return node != null && node.data().getString( SchemaNodePropertyNames.DESCRIPTOR ) != null;
    }

    @Override
    public boolean hasCmsDescriptor( final ApplicationKey applicationKey )
    {
        return ApplicationHelper.runAsAdmin( () -> this.nodeService.nodeExists( cmsDescriptorNodePath( applicationKey ) ) );
    }

    @Override
    public Node upsertApplicationDescriptor( final ApplicationKey applicationKey, final String descriptor, final IconUpdate iconUpdate )
    {
        return ApplicationHelper.runAsAdmin( () -> {
            final NodePath appPath = applicationNodePath( applicationKey );
            if ( !this.nodeService.nodeExists( appPath ) )
            {
                throw new NodeNotFoundException( String.format( "Application node [%s] not found", appPath ) );
            }
            final Node node = writeApplicationDescriptor( appPath, descriptor, iconUpdate );
            this.nodeService.refresh( RefreshMode.ALL );
            return node;
        } );
    }

    /**
     * The descriptor is a property of the application node, the icon a binary of it named by its format ({@code enonic.svg} or
     * {@code enonic.png}), whatever the bundle shipped it as. A binary attached under the previous name (svg replaced by png) is no
     * longer referenced and dropped by the update.
     */
    private Node writeApplicationDescriptor( final NodePath appPath, final String descriptor, final IconUpdate iconUpdate )
    {
        final BinaryReference iconReference = iconUpdate instanceof IconUpdate.Replace replace ? appIconReference( replace ) : null;

        final UpdateNodeParams.Builder params = UpdateNodeParams.create().path( appPath ).editor( toBeEdited -> {
            toBeEdited.data.setString( SchemaNodePropertyNames.DESCRIPTOR, descriptor );
            switch ( iconUpdate )
            {
                case IconUpdate.Keep _ ->
                {
                }
                case IconUpdate.Remove _ -> toBeEdited.data.removeProperties( SchemaNodePropertyNames.ICON );
                case IconUpdate.Replace _ -> toBeEdited.data.setBinaryReference( SchemaNodePropertyNames.ICON, iconReference );
            }
        } ).refresh( RefreshMode.ALL );

        if ( iconUpdate instanceof IconUpdate.Replace replace )
        {
            params.attachBinary( iconReference, replace.data() );
        }
        return this.nodeService.update( params.build() );
    }

    private static BinaryReference appIconReference( final IconUpdate.Replace replace )
    {
        final BinaryReference reference = SchemaResourcePaths.appIconBinaryReference( replace.mimeType() );
        if ( reference == null )
        {
            throw new IllegalArgumentException( "unsupported icon mime type: " + replace.mimeType() );
        }
        return reference;
    }

    /**
     * Removes the {@code cms} subtree and the descriptor and icon from the application node, whichever of them exist.
     */
    private void deletePersistedSchema( final NodePath appPath )
    {
        final NodePath cmsPath = new NodePath( appPath, SchemaNodeLayout.CMS_NODE_PATH.getName() );
        if ( this.nodeService.nodeExists( cmsPath ) )
        {
            this.nodeService.delete( DeleteNodeParams.create().nodePath( cmsPath ).refresh( RefreshMode.ALL ).build() );
        }

        final Node appNode = this.nodeService.getByPath( appPath );
        if ( appNode != null && ( appNode.data().getString( SchemaNodePropertyNames.DESCRIPTOR ) != null ||
            appNode.data().getBinaryReference( SchemaNodePropertyNames.ICON ) != null ) )
        {
            this.nodeService.update( UpdateNodeParams.create().path( appPath ).editor( toBeEdited -> {
                toBeEdited.data.removeProperties( SchemaNodePropertyNames.DESCRIPTOR );
                toBeEdited.data.removeProperties( SchemaNodePropertyNames.ICON );
            } ).refresh( RefreshMode.ALL ).build() );
        }
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
            () -> this.nodeService.list( ListNodesParams.create().parentPath( APPLICATIONS_ROOT ).build() )
                .filter( entry -> APPLICATIONS_ROOT.equals( entry.nodePath().getParentPath() ) )
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
        return this.nodeService.getByPath( new NodePath( APPLICATIONS_ROOT, NodeName.from( applicationName ) ) );
    }
}
