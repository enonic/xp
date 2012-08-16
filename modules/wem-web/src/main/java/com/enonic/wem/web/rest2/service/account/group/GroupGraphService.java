package com.enonic.wem.web.rest2.service.account.group;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.rest2.resource.account.graph.GraphResult;
import com.enonic.wem.web.rest2.service.account.AccountGraphService;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;

@Component
public class GroupGraphService
    extends AccountGraphService
{

    public GraphResult generateGraph( GroupEntity entity )
    {
        setTimestamp( String.valueOf( System.currentTimeMillis() ) );
        return generateGraph( entity, null, -1 );
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
            for ( GroupEntity group : entity.getMembers( false ) )
            {
                if ( graph.containsEntity( group ) )
                {
                    // Skip current iteration because we've already put this group into the graph
                    continue;
                }
                if ( group.isOfType( GroupType.USER, false ) )
                {
                    graph.addAccountNode( buildGraphData( group.getUser() ) );
                }
                else
                {
                    generateGraph( group, graph, level - 1 );
                }
            }
        }

        return graph;
    }

    protected ObjectNode buildGraphData( GroupEntity groupEntity )
    {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put( GraphResult.ID_PARAM, generateId( groupEntity.getGroupKey().toString() ) );

        node.put( GraphResult.NAME_PARAM, groupEntity.getName() );

        node.put( GraphResult.DATA_PARAM,
                  createGraphData( String.valueOf( groupEntity.getGroupKey() ), groupEntity.isBuiltIn() ? "role" : "group",
                                   groupEntity.isBuiltIn(), groupEntity.getName() ) );
        node.put( GraphResult.ADJACENCIES_PARAM, createGraphAdjacencies( groupEntity.getMembers( false ) ) );
        return node;
    }

    protected ObjectNode buildGraphData( UserEntity userEntity )
    {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put( GraphResult.ID_PARAM, generateId( userEntity.getKey().toString() ) );

        node.put( GraphResult.NAME_PARAM, userEntity.getDisplayName() );

        node.put( GraphResult.DATA_PARAM,
                  createGraphData( String.valueOf( userEntity.getKey() ), "user", userEntity.isBuiltIn(), userEntity.getName() ) );
        return node;
    }


}

