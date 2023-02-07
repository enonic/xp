package com.enonic.xp.core.impl.app.resolver;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.NodeValueResource;
import com.enonic.xp.core.impl.app.VirtualAppConstants;
import com.enonic.xp.core.impl.app.VirtualAppContext;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.query.expr.CompareExpr;
import com.enonic.xp.query.expr.FieldExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.expr.ValueExpr;
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
        final QueryExpr query = QueryExpr.from( CompareExpr.like( FieldExpr.from( "_path" ), ValueExpr.string(
            "/" + applicationKey + "/" + VirtualAppConstants.SITE_ROOT_NAME + "/*/*/*" ) ) );

        return VirtualAppContext.createContext().callWith( () -> {
            final FindNodesByQueryResult nodes = this.nodeService.findByQuery( NodeQuery.create().query( query ).withPath( true ).build() );

            return nodes.getNodeHits()
                .stream()
                .map( nodeHit -> nodeHit.getNodePath()
                    .removeFromBeginning( NodePath.create( applicationKey.toString() ).build() )
                    .asAbsolute()
                    .toString() )
                .collect( Collectors.toCollection( LinkedHashSet::new ) );
        } );
    }

    @Override
    public Resource findResource( final String path )
    {
        if ( !path.startsWith( "/" + VirtualAppConstants.SITE_ROOT_NAME + "/" ) )
        {
            return null;
        }

        final NodePath appPath = NodePath.create( NodePath.ROOT, applicationKey.getName() ).build();

        final Node resourceNode =
            VirtualAppContext.createContext().callWith( () -> nodeService.getByPath( NodePath.create( appPath, path ).build() ) );

        if ( resourceNode == null )
        {
            if ( VirtualAppConstants.SITE_RESOURCE_PATH.equals( path ) )
            {
                final Node applicationNode = VirtualAppContext.createContext().callWith( () -> nodeService.getByPath( appPath ) );

                return applicationNode != null ? new NodeValueResource( ResourceKey.from( applicationKey, path ),
                                                                        VirtualAppConstants.DEFAULT_SITE_RESOURCE_VALUE,
                                                                        applicationNode.getTimestamp() ) : null;
            }
            else
            {
                return null;
            }
        }

        return new NodeValueResource( ResourceKey.from( applicationKey, path ), resourceNode );
    }
}
