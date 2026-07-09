package com.enonic.xp.core.impl.app.resolver;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.NodeValueResource;
import com.enonic.xp.core.impl.schema.NamespaceConstants;
import com.enonic.xp.core.impl.schema.NamespaceContext;
import com.enonic.xp.node.ListNodesParams;
import com.enonic.xp.node.ListNodesResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeListEntry;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

public final class NodeResourceApplicationUrlResolver
    implements ApplicationUrlResolver
{
    private final ApplicationKey applicationKey;

    private final NodeService nodeService;

    public NodeResourceApplicationUrlResolver( final ApplicationKey applicationKey, final NodeService nodeService )
    {
        this.applicationKey = applicationKey;
        this.nodeService = nodeService;
    }

    @Override
    public Set<String> findFiles()
    {
        final NodePath cmsPath = NodePath.create( NamespaceConstants.NAMESPACE_APP_ROOT_PARENT )
            .addElement( applicationKey.toString() )
            .addElement( NamespaceConstants.CMS_ROOT_NAME )
            .build();

        return NamespaceContext.createAdminContext().callWith( () -> {
            final ListNodesResult nodes =
                this.nodeService.list( ListNodesParams.create().parentPath( cmsPath ).recursive( true ).build() );

            return nodes.getEntries()
                .stream()
                .map( NodeListEntry::nodePath )
                .filter( nodePath -> isResource( cmsPath, nodePath ) )
                .map( nodePath -> nodePath.toString().substring( nodePath.toString().indexOf( '/', 1 ) ) )
                .collect( Collectors.toCollection( LinkedHashSet::new ) );
        } );
    }

    /**
     * A resource is a leaf three levels below the cms root - {@code <resource root>/<name>/<file>}. The listing walks the whole subtree,
     * so the folders on the way there are skipped here.
     */
    private static boolean isResource( final NodePath cmsPath, final NodePath nodePath )
    {
        return cmsPath.equals( nodePath.getParentPath().getParentPath().getParentPath() );
    }

    @Override
    public Resource findResource( final String path )
    {
        if ( !path.startsWith( "/" + NamespaceConstants.CMS_ROOT_NAME + "/" ) )
        {
            return null;
        }

        final NodePath appPath = new NodePath( NamespaceConstants.NAMESPACE_APP_ROOT_PARENT, NodeName.from( applicationKey.toString() ) );

        final NodePath.Builder builder = NodePath.create( appPath );

        Arrays.stream( path.split( "/" ) ).forEach( builder::addElement );

        final Node resourceNode = NamespaceContext.createAdminContext().callWith( () -> nodeService.getByPath( builder.build() ) );

        if ( resourceNode == null )
        {
            return null;
        }
        else
        {
            return new NodeValueResource( ResourceKey.from( applicationKey, path ), resourceNode );
        }
    }
}
