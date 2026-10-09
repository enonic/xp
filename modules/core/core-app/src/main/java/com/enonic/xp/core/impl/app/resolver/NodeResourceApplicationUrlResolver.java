package com.enonic.xp.core.impl.app.resolver;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.Context;
import com.enonic.xp.core.impl.app.NodeValueResource;
import com.enonic.xp.core.impl.app.SchemaResourceNames;
import com.enonic.xp.core.impl.app.SchemaResourcePaths;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.SchemaNodePropertyNames;

/**
 * Serves application resources stored as nodes below the application node: the {@code cms} subtree and the persisted
 * application descriptor ({@code enonic.yaml}). The application icon is attached to the descriptor node and served as the
 * virtual resource {@code enonic.svg} or {@code enonic.png}, by its mime type.
 * Resource paths are relative to the application node, e.g. {@code /cms/content-types/mytype/mytype.yaml}.
 */
public final class NodeResourceApplicationUrlResolver
    implements ApplicationUrlResolver
{
    private final ApplicationKey applicationKey;

    private final NodeService nodeService;

    private final NodePath appNodePath;

    private final Supplier<Context> contextSupplier;

    public NodeResourceApplicationUrlResolver( final ApplicationKey applicationKey, final NodeService nodeService, final NodePath appNodePath,
                                               final Supplier<Context> contextSupplier )
    {
        this.applicationKey = applicationKey;
        this.nodeService = nodeService;
        this.appNodePath = appNodePath;
        this.contextSupplier = contextSupplier;
    }

    @Override
    public Set<String> findFiles()
    {
        final int appPathLength = appNodePath.toString().length();

        return contextSupplier.get().callWith( () -> {
            final Set<String> files = this.nodeService.list( ListNodesParams.create().parentPath( appNodePath ).build() )
                .map( NodeListEntry::nodePath )
                .filter( NodeResourceApplicationUrlResolver::isResource )
                .map( nodePath -> nodePath.toString().substring( appPathLength ) )
                // the icon is not a node of its own: a node at its path is never served, the virtual icon is listed instead
                .filter( path -> isServedPath( path ) && !SchemaResourcePaths.isAppIconPath( path ) )
                .collect( Collectors.toCollection( LinkedHashSet::new ) );

            if ( files.contains( "/" + SchemaResourcePaths.APP_DESCRIPTOR_NAME ) )
            {
                final String iconName = appIconName( descriptorNode() );
                if ( iconName != null )
                {
                    files.add( "/" + iconName );
                }
            }
            return files;
        } );
    }

    /**
     * A resource is a file node (its name has an extension); nodes without an extension are folders on the way to a resource.
     */
    private static boolean isResource( final NodePath nodePath )
    {
        return nodePath.getName().toString().contains( "." );
    }

    /**
     * Only the {@code cms} subtree and the persisted application descriptor and icon are served, never other children
     * of the application node.
     */
    private static boolean isServedPath( final String path )
    {
        return path.startsWith( "/" + SchemaResourceNames.CMS_ROOT_NAME + "/" ) || SchemaResourcePaths.isPersistedRootResource( path );
    }

    @Override
    public Resource findResource( final String path )
    {
        if ( !isServedPath( path ) )
        {
            return null;
        }

        if ( SchemaResourcePaths.isAppIconPath( path ) )
        {
            return contextSupplier.get().callWith( () -> findAppIcon( path ) );
        }

        final NodePath.Builder builder = NodePath.create( appNodePath );

        Arrays.stream( path.split( "/" ) ).forEach( builder::addElement );

        return contextSupplier.get().callWith( () -> {
            final Node resourceNode = nodeService.getByPath( builder.build() );

            if ( resourceNode == null )
            {
                return null;
            }

            final ResourceKey resourceKey = ResourceKey.from( applicationKey, path );

            // an icon node references its binary in the data; the binaries attached to a node are not consulted, a binary
            // may remain attached after its reference is removed from the data
            if ( SchemaResourceNames.ICON_BINARY_REFERENCE.equals( resourceNode.data().getBinaryReference( SchemaNodePropertyNames.ICON ) ) )
            {
                final ByteSource binary = nodeService.getBinary( resourceNode.id(), SchemaResourceNames.ICON_BINARY_REFERENCE );
                return new NodeValueResource( resourceKey, binary, resourceNode.getTimestamp() );
            }

            return new NodeValueResource( resourceKey, resourceNode );
        } );
    }

    /**
     * The application icon attached to the descriptor node, served at the virtual path matching its mime type only.
     */
    private Resource findAppIcon( final String path )
    {
        final Node descriptorNode = descriptorNode();
        final String iconName = appIconName( descriptorNode );
        if ( iconName == null || !path.equals( "/" + iconName ) )
        {
            return null;
        }

        final ByteSource binary = nodeService.getBinary( descriptorNode.id(), SchemaResourceNames.APP_ICON_BINARY_REFERENCE );
        return new NodeValueResource( ResourceKey.from( applicationKey, path ), binary, descriptorNode.getTimestamp() );
    }

    private Node descriptorNode()
    {
        return nodeService.getByPath( new NodePath( appNodePath, NodeName.from( SchemaResourcePaths.APP_DESCRIPTOR_NAME ) ) );
    }

    private static String appIconName( final Node descriptorNode )
    {
        if ( descriptorNode == null ||
            !SchemaResourceNames.APP_ICON_BINARY_REFERENCE.equals( descriptorNode.data().getBinaryReference( SchemaNodePropertyNames.ICON ) ) )
        {
            return null;
        }
        return SchemaResourcePaths.appIconName( descriptorNode.data().getString( SchemaNodePropertyNames.MIME_TYPE ) );
    }
}