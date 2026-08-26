package com.enonic.xp.core.impl.app.resolver;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.Context;
import com.enonic.xp.core.impl.app.NodeValueResource;
import com.enonic.xp.core.impl.app.SchemaNodeLayout;
import com.enonic.xp.core.impl.app.SchemaResourcePaths;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.SchemaNodePropertyNames;
import com.enonic.xp.util.BinaryReference;

/**
 * Serves the application resources persisted in the application repository, laid out as {@link SchemaNodeLayout} describes:
 * the application descriptor and icon from the application node, the {@code cms} schema from the nodes below {@code cms}.
 * The resources are served at the paths the application ships them at, e.g. {@code /enonic.yaml},
 * {@code /cms/content-types/mytype.yaml} and {@code /cms/content-types/mytype.svg} for the icon of that content type.
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
        return contextSupplier.get().callWith( () -> {
            final Set<String> files = new LinkedHashSet<>();

            final Node appNode = nodeService.getByPath( appNodePath );
            if ( appNode != null )
            {
                if ( appNode.data().getString( SchemaNodePropertyNames.DESCRIPTOR ) != null )
                {
                    files.add( "/" + SchemaResourcePaths.APP_DESCRIPTOR_NAME );
                }
                final String iconName = SchemaResourcePaths.appIconName( appNode.data().getBinaryReference( SchemaNodePropertyNames.ICON ) );
                if ( iconName != null )
                {
                    files.add( "/" + iconName );
                }
            }

            for ( final Node schemaNode : schemaNodes() )
            {
                final SchemaNodeLayout.Location location = SchemaNodeLayout.locate( schemaNode.name() );
                if ( location == null )
                {
                    continue;
                }
                files.add( location.kind().resourcePath( location.name() ) );
                final BinaryReference icon = schemaNode.data().getBinaryReference( SchemaNodePropertyNames.ICON );
                if ( icon != null && location.kind().hasIcon() )
                {
                    files.add( location.kind().iconPath( location.name(), SchemaNodeLayout.iconExtension( icon ) ) );
                }
            }
            return files;
        } );
    }

    // the children of the cms node, nothing deeper is persisted
    private Iterable<Node> schemaNodes()
    {
        final NodePath cmsPath = cmsNodePath();
        final NodeIds nodeIds = nodeService.list( ListNodesParams.create().parentPath( cmsPath ).build() )
            .filter( entry -> cmsPath.equals( entry.nodePath().getParentPath() ) )
            .map( NodeListEntry::nodeId )
            .collect( NodeIds.collector() );
        return nodeIds.isEmpty() ? Set.of() : nodeService.getByIds( nodeIds );
    }

    @Override
    public Resource findResource( final String path )
    {
        final SchemaNodeLayout.Location location = SchemaNodeLayout.locate( path );
        if ( location == null )
        {
            return null;
        }
        return contextSupplier.get().callWith( () -> location.isApplicationNode() ? findApplicationResource( path, location ) : findSchemaResource( path, location ) );
    }

    private Resource findApplicationResource( final String path, final SchemaNodeLayout.Location location )
    {
        final Node appNode = nodeService.getByPath( appNodePath );
        if ( appNode == null )
        {
            return null;
        }
        final ResourceKey resourceKey = ResourceKey.from( applicationKey, path );
        if ( location.icon() )
        {
            // served at the path matching the name of its binary only; the data decides, a binary may remain attached after
            // its reference is removed
            final BinaryReference reference = appNode.data().getBinaryReference( SchemaNodePropertyNames.ICON );
            final String iconName = SchemaResourcePaths.appIconName( reference );
            if ( iconName == null || !resourceKey.getPath().equals( "/" + iconName ) )
            {
                return null;
            }
            return new NodeValueResource( resourceKey, nodeService.getBinary( appNode.id(), reference ), appNode.getTimestamp() );
        }
        final String descriptor = appNode.data().getString( SchemaNodePropertyNames.DESCRIPTOR );
        return descriptor == null
            ? null
            : new NodeValueResource( resourceKey, ByteSource.wrap( descriptor.getBytes( StandardCharsets.UTF_8 ) ), appNode.getTimestamp() );
    }

    private Resource findSchemaResource( final String path, final SchemaNodeLayout.Location location )
    {
        final Node schemaNode = nodeService.getByPath( new NodePath( cmsNodePath(), location.nodePath().getName() ) );
        if ( schemaNode == null )
        {
            return null;
        }
        final ResourceKey resourceKey = ResourceKey.from( applicationKey, path );
        if ( location.icon() )
        {
            final BinaryReference reference = schemaNode.data().getBinaryReference( SchemaNodePropertyNames.ICON );
            if ( reference == null || !resourceKey.getPath().endsWith( "." + SchemaNodeLayout.iconExtension( reference ) ) )
            {
                return null;
            }
            return new NodeValueResource( resourceKey, nodeService.getBinary( schemaNode.id(), reference ), schemaNode.getTimestamp() );
        }
        return new NodeValueResource( resourceKey, schemaNode );
    }

    private NodePath cmsNodePath()
    {
        return new NodePath( appNodePath, SchemaNodeLayout.CMS_NODE_PATH.getName() );
    }
}
