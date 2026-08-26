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
import com.enonic.xp.core.impl.app.VirtualAppConstants;
import com.enonic.xp.core.impl.app.VirtualAppContext;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

/**
 * Serves application resources stored as nodes below {@code <appNodePath>/cms}.
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

    /**
     * Resolver for a virtual application stored in the {@code system.app} repository.
     */
    public static NodeResourceApplicationUrlResolver forVirtualApp( final ApplicationKey applicationKey, final NodeService nodeService )
    {
        return new NodeResourceApplicationUrlResolver( applicationKey, nodeService, new NodePath( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT,
                                                                                                  NodeName.from( applicationKey.toString() ) ),
                                                       VirtualAppContext::createContext );
    }

    @Override
    public Set<String> findFiles()
    {
        final NodePath cmsPath = new NodePath( appNodePath, NodeName.from( VirtualAppConstants.CMS_ROOT_NAME ) );
        final int appPathLength = appNodePath.toString().length();

        return contextSupplier.get().callWith( () -> {
            return this.nodeService.list( ListNodesParams.create().parentPath( cmsPath ).build() )
                .map( NodeListEntry::nodePath )
                .filter( NodeResourceApplicationUrlResolver::isResource )
                .map( nodePath -> nodePath.toString().substring( appPathLength ) )
                .collect( Collectors.toCollection( LinkedHashSet::new ) );
        } );
    }

    /**
     * A resource is a file node (its name has an extension); nodes without an extension are folders on the way to a resource.
     */
    private static boolean isResource( final NodePath nodePath )
    {
        return nodePath.getName().toString().contains( "." );
    }

    @Override
    public Resource findResource( final String path )
    {
        if ( !path.startsWith( "/" + VirtualAppConstants.CMS_ROOT_NAME + "/" ) )
        {
            return null;
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

            if ( resourceNode.getAttachedBinaries().getByBinaryReference( VirtualAppConstants.ICON_BINARY_REFERENCE ) != null )
            {
                final ByteSource binary = nodeService.getBinary( resourceNode.id(), VirtualAppConstants.ICON_BINARY_REFERENCE );
                return new NodeValueResource( resourceKey, binary, resourceNode.getTimestamp() );
            }

            return new NodeValueResource( resourceKey, resourceNode );
        } );
    }
}