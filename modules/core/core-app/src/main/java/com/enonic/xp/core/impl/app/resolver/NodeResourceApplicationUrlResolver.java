package com.enonic.xp.core.impl.app.resolver;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.app.NodeValueResource;
import com.enonic.xp.core.impl.app.VirtualAppConstants;
import com.enonic.xp.core.impl.app.VirtualAppContext;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.query.expr.DslExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.schema.SchemaNodePropertyNames;

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
        PropertyTree request = new PropertyTree();

        final PropertySet likeExpression = request.addSet( "like" );
        likeExpression.addString( "field", "_path" );
        likeExpression.addString( "value", "/" + applicationKey + "/" + VirtualAppConstants.SITE_ROOT_NAME + "/*/*/*" );

        return VirtualAppContext.createContext().callWith( () -> {
            final FindNodesByQueryResult nodes = this.nodeService.findByQuery(
                NodeQuery.create().query( QueryExpr.from( DslExpr.from( request ) ) ).withPath( true ).build() );

            return nodes.getNodeHits()
                .stream()
                .map( NodeHit::getNodePath )
                .map( nodePath -> nodePath.toString().substring( nodePath.toString().indexOf( "/", 1 ) ) )
                .collect( Collectors.toSet() );
        } );

    }

    @Override
    public Resource findResource( final String path )
    {
        if ( !path.startsWith( "/" + VirtualAppConstants.SITE_ROOT_NAME + "/" ) )
        {
            return null;
        }

        final NodePath.Builder builder =
            NodePath.create( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT ).addElement( applicationKey.toString() );

        Arrays.stream( path.split( "/" ) ).forEach( builder::addElement );

        final Node resourceNode = VirtualAppContext.createContext().callWith( () -> nodeService.getByPath( builder.build() ) );

        if ( VirtualAppConstants.SITE_RESOURCE_PATH.equals( path ) )
        {
            if ( resourceNode == null || resourceNode.data().getValue( SchemaNodePropertyNames.RESOURCE ) == null )
            {
                final NodePath appPath =
                    new NodePath( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT, NodeName.from( applicationKey.toString() ) );
                final Node applicationNode = VirtualAppContext.createContext().callWith( () -> nodeService.getByPath( appPath ) );
                return new NodeValueResource( ResourceKey.from( applicationKey, path ), VirtualAppConstants.DEFAULT_SITE_RESOURCE_VALUE,
                                              applicationNode.getTimestamp() );
            }
        }

        return resourceNode != null ? new NodeValueResource( ResourceKey.from( applicationKey, path ), resourceNode ) : null;
    }
}
