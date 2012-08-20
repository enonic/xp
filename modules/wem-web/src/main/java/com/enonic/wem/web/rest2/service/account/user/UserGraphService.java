package com.enonic.wem.web.rest2.service.account.user;

import java.util.Collection;

import javax.annotation.Nullable;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import com.enonic.wem.web.rest2.resource.account.graph.GraphResult;
import com.enonic.wem.web.rest2.service.account.group.GroupGraphService;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;

@Component
public class UserGraphService
    extends GroupGraphService
{

    public GraphResult generateGraph( UserEntity user )
    {
        setTimestamp( String.valueOf( System.currentTimeMillis() ) );
        return generateGraph( user, null, -1 );
    }

    private GraphResult generateGraph( UserEntity user, GraphResult graph, int level )
    {
        if ( graph == null )
        {
            graph = new GraphResult();
        }
        graph.addAccountNode( buildGraphData( user ) );
        for ( GroupEntity group : user.getDirectMemberships() )
        {
            generateGraph( group, graph, level - 1 );
        }

        return graph;
    }


    private GraphResult generateGraph( GroupEntity entity, GraphResult graph, int level )
    {
        if ( graph == null )
        {
            graph = new GraphResult();
        }
        ObjectNode groupNode = buildGraphData( entity );
        graph.addAccountNode( groupNode );

        if ( level != 0 )
        {
            for ( GroupEntity group : entity.getMemberships( false ) )
            {
                if ( graph.containsEntity( getTimestamp(), group ) )
                {
                    // Skip current iteration because we've already put this group into the graph
                    continue;
                }
                if ( !group.isOfType( GroupType.USER, false ) )
                {
                    generateGraph( group, graph, level - 1 );
                }
            }
        }

        return graph;
    }


    protected ObjectNode buildGraphData( GroupEntity groupEntity )
    {
        ObjectNode node = super.buildGraphData( groupEntity );
        node.put( GraphResult.ADJACENCIES_PARAM, createGraphAdjacencies( groupEntity.getMemberships( false ) ) );
        return node;
    }

    protected ObjectNode buildGraphData( UserEntity userEntity )
    {
        ObjectNode node = super.buildGraphData( userEntity );
        node.put( GraphResult.ADJACENCIES_PARAM, createGraphAdjacencies( userEntity.getDirectMemberships() ) );
        return node;
    }

    protected ArrayNode createGraphAdjacencies( Collection<GroupEntity> memberships )
    {
        Collection<GroupEntity> filteredMemberships = Collections2.filter( memberships, new Predicate<GroupEntity>()
        {
            public boolean apply( @Nullable GroupEntity input )
            {
                return !input.isOfType( GroupType.USER, true );
            }
        } );
        return super.createGraphAdjacencies( filteredMemberships );
    }

}
