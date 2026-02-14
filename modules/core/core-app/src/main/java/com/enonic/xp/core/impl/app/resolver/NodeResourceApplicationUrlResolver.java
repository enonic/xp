package com.enonic.xp.core.impl.app.resolver;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.NodeValueResource;
import com.enonic.xp.core.impl.app.VirtualAppConstants;
import com.enonic.xp.core.impl.app.VirtualAppContext;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeName;
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
            "/" + applicationKey + "/" + VirtualAppConstants.CMS_ROOT_NAME + "/*/*/*" ) ) );

        return VirtualAppContext.createContext().callWith( () -> {
            final FindNodesByQueryResult nodes = this.nodeService.findByQuery( NodeQuery.create().query( query ).withPath( true ).build() );

            return nodes.getNodeHits()
                .stream()
                .map( NodeHit::getNodePath )
                .map( nodePath -> nodePath.toString().substring( nodePath.toString().indexOf( '/', 1 ) ) )
                .collect( Collectors.toCollection( LinkedHashSet::new ) );
        } );
    }

    @Override
    public Resource findResource( final String path )
    {
        if ( !path.startsWith( "/" + VirtualAppConstants.CMS_ROOT_NAME + "/" ) )
        {
            return null;
        }

        final NodePath appPath = new NodePath( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT, NodeName.from( applicationKey.toString() ) );

        final NodePath.Builder builder = NodePath.create( appPath );

        Arrays.stream( path.split( "/" ) ).forEach( builder::addElement );

        final Node resourceNode = VirtualAppContext.createContext().callWith( () -> nodeService.getByPath( builder.build() ) );

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
