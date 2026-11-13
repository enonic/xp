package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.List;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.SchemaNodeLayout.Kind;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.icon.IconUpdate;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.SchemaNodePropertyNames;
import com.enonic.xp.util.BinaryReference;

/**
 * Reads and writes the schema of an application as nodes below its {@code cms} node, laid out as {@link SchemaNodeLayout}
 * describes: one node per schema, holding the descriptor as text in the {@code resource} property and the icon as a binary.
 * Every write is a single node operation, so a schema is never seen half written.
 */
final class DynamicResourceManager
{
    private final NodeService nodeService;

    DynamicResourceManager( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    /**
     * A schema node as written: created when there was none, updated otherwise.
     */
    record Stored(Node node, boolean created)
    {
    }

    /**
     * The node of a schema, {@code null} when there is none.
     */
    Node get( final ApplicationKey applicationKey, final Kind kind, final String name )
    {
        return ApplicationHelper.runAsAdmin( () -> nodeService.getByPath( nodePath( applicationKey, kind, name ) ) );
    }

    /**
     * Creates the schema node, or updates it when there is one: the descriptor is replaced when a {@code resource} is given and
     * kept otherwise, the icon is kept, removed or replaced as {@code iconUpdate} tells. A create racing another create of the same
     * schema ends up as an update.
     */
    Stored upsert( final ApplicationKey applicationKey, final Kind kind, final String name, final String resource,
                   final IconUpdate iconUpdate )
    {
        return ApplicationHelper.runAsAdmin( () -> {
            final NodePath path = nodePath( applicationKey, kind, name );
            if ( !nodeService.nodeExists( path ) )
            {
                if ( resource == null )
                {
                    throw new IllegalArgumentException( String.format( "resource is required to create [%s]", path ) );
                }
                try
                {
                    return changed( applicationKey, new Stored( create( path, resource, iconUpdate ), true ) );
                }
                catch ( NodeAlreadyExistAtPathException e )
                {
                    // created by a racing call: updated instead
                }
            }
            return changed( applicationKey, new Stored( update( path, resource, iconUpdate ), false ) );
        } );
    }

    // the schema of the application was written: the version of the application node moves, what every read of the schema cached
    // on any node of the cluster is checked against, see ApplicationRepoServiceImpl.markSchemaChanged
    private <T> T changed( final ApplicationKey applicationKey, final T result )
    {
        ApplicationRepoServiceImpl.markSchemaChanged( nodeService, applicationKey );
        return result;
    }

    private Node create( final NodePath path, final String resource, final IconUpdate iconUpdate )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( SchemaNodePropertyNames.RESOURCE, resource );

        final CreateNodeParams.Builder params = CreateNodeParams.create()
            .parent( path.getParentPath() )
            .name( path.getName() )
            .data( data )
            .inheritPermissions( true )
            .refresh( RefreshMode.ALL );

        if ( iconUpdate instanceof IconUpdate.Replace replace )
        {
            final BinaryReference reference = iconReference( replace );
            data.setBinaryReference( SchemaNodePropertyNames.ICON, reference );
            params.attachBinary( reference, replace.data() );
        }
        return nodeService.create( params.build() );
    }

    private Node update( final NodePath path, final String resource, final IconUpdate iconUpdate )
    {
        final BinaryReference reference = iconUpdate instanceof IconUpdate.Replace replace ? iconReference( replace ) : null;

        final UpdateNodeParams.Builder params = UpdateNodeParams.create().path( path ).editor( toBeEdited -> {
            if ( resource != null )
            {
                toBeEdited.data.setString( SchemaNodePropertyNames.RESOURCE, resource );
            }
            switch ( iconUpdate )
            {
                case IconUpdate.Keep _ ->
                {
                }
                case IconUpdate.Remove _ -> toBeEdited.data.removeProperties( SchemaNodePropertyNames.ICON );
                case IconUpdate.Replace _ -> toBeEdited.data.setBinaryReference( SchemaNodePropertyNames.ICON, reference );
            }
        } ).refresh( RefreshMode.ALL );

        if ( iconUpdate instanceof IconUpdate.Replace replace )
        {
            // a binary attached under the other format's name is no longer referenced and dropped by the update
            params.attachBinary( reference, replace.data() );
        }
        return nodeService.update( params.build() );
    }

    private static BinaryReference iconReference( final IconUpdate.Replace replace )
    {
        final BinaryReference reference = SchemaNodeLayout.iconReference( replace.mimeType() );
        if ( reference == null )
        {
            throw new IllegalArgumentException( String.format( "unsupported icon mime type: %s", replace.mimeType() ) );
        }
        return reference;
    }

    /**
     * @return {@code false} when there is no such schema
     */
    boolean delete( final ApplicationKey applicationKey, final Kind kind, final String name )
    {
        return ApplicationHelper.runAsAdmin( () -> {
            final NodePath path = nodePath( applicationKey, kind, name );
            final boolean deleted = nodeService.nodeExists( path ) &&
                nodeService.delete( DeleteNodeParams.create().nodePath( path ).refresh( RefreshMode.ALL ).build() ).getNodeIds().isNotEmpty();
            return deleted ? changed( applicationKey, true ) : false;
        } );
    }

    /**
     * The nodes of a kind, by name.
     */
    List<Node> list( final ApplicationKey applicationKey, final Kind kind )
    {
        return ApplicationHelper.runAsAdmin( () -> {
            final NodePath cmsPath = ApplicationRepoServiceImpl.cmsNodePath( applicationKey );
            final NodeIds nodeIds = nodeService.list( ListNodesParams.create().parentPath( cmsPath ).build() )
                .filter( entry -> cmsPath.equals( entry.nodePath().getParentPath() ) )
                .filter( entry -> {
                    final SchemaNodeLayout.Location location = SchemaNodeLayout.locate( entry.nodePath().getName() );
                    return location != null && location.kind() == kind;
                } )
                .map( NodeListEntry::nodeId )
                .collect( NodeIds.collector() );
            return nodeIds.isEmpty()
                ? List.of()
                : nodeService.getByIds( nodeIds ).stream().sorted( Comparator.comparing( node -> node.name().toString() ) ).toList();
        } );
    }

    /**
     * Creates the {@code cms} node, the marker of an application owning a schema, with the cms descriptor below it, in that order:
     * the descriptor is there once the schema can be written.
     */
    Node createCms( final ApplicationKey applicationKey, final String resource )
    {
        return ApplicationHelper.runAsAdmin( () -> {
            final NodePath cmsPath = ApplicationRepoServiceImpl.cmsNodePath( applicationKey );
            if ( !nodeService.nodeExists( cmsPath ) )
            {
                nodeService.create( CreateNodeParams.create()
                                        .name( cmsPath.getName() )
                                        .parent( cmsPath.getParentPath() )
                                        .inheritPermissions( true )
                                        .refresh( RefreshMode.ALL )
                                        .build() );
            }
            return changed( applicationKey, create( nodePath( applicationKey, Kind.CMS, null ), resource, IconUpdate.KEEP ) );
        } );
    }

    /**
     * Deletes the {@code cms} node with the whole schema below it.
     *
     * @return {@code false} when the application owns no schema
     */
    boolean deleteCms( final ApplicationKey applicationKey )
    {
        return ApplicationHelper.runAsAdmin( () -> {
            final NodePath cmsPath = ApplicationRepoServiceImpl.cmsNodePath( applicationKey );
            final boolean deleted = nodeService.nodeExists( cmsPath ) &&
                nodeService.delete( DeleteNodeParams.create().nodePath( cmsPath ).refresh( RefreshMode.ALL ).build() ).getNodeIds().isNotEmpty();
            return deleted ? changed( applicationKey, true ) : false;
        } );
    }

    boolean nodeExists( final NodePath nodePath )
    {
        return ApplicationHelper.runAsAdmin( () -> nodeService.nodeExists( nodePath ) );
    }

    /**
     * The descriptor held by a schema node, as the resource it is served as.
     */
    static Resource toResource( final ApplicationKey applicationKey, final Node node )
    {
        final SchemaNodeLayout.Location location = SchemaNodeLayout.locate( node.name() );
        return new NodeValueResource( ResourceKey.from( applicationKey, location.kind().resourcePath( location.name() ) ), node );
    }

    /**
     * The icon attached to a schema node, {@code null} when it has none.
     */
    Icon iconOf( final Node node )
    {
        final BinaryReference reference = node.data().getBinaryReference( SchemaNodePropertyNames.ICON );
        final String mimeType = SchemaNodeLayout.iconMimeType( reference );
        if ( mimeType == null )
        {
            return null;
        }
        final ByteSource binary = ApplicationHelper.runAsAdmin( () -> nodeService.getBinary( node.id(), reference ) );
        try
        {
            return Icon.from( binary.read(), mimeType, node.getTimestamp() );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private static NodePath nodePath( final ApplicationKey applicationKey, final Kind kind, final String name )
    {
        return ApplicationRepoServiceImpl.schemaNodePath( applicationKey, kind.nodePath( name ) );
    }
}
