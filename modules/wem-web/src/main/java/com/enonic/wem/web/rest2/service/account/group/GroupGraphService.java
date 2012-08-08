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

    public GraphResult buildGraph( GroupEntity entity )
    {
        setParentKey( System.currentTimeMillis() + "_" + String.valueOf( entity.getGroupKey() ) );
        GraphResult groupResult = new GraphResult();
        ObjectNode groupData = buildGroupData( entity );
        groupResult.addAccountNode( groupData );

        for ( GroupEntity group : entity.getAllMembersRecursively() )
        {
            ObjectNode node;
            if ( group.isOfType( GroupType.USER, false ) )
            {
                node = buildGraphData( group.getUser() );
            }
            else
            {
                node = buildGraphData( group );
            }
            groupResult.addAccountNode( node );
        }

        return groupResult;
    }

    protected ObjectNode buildGroupData( GroupEntity groupEntity )
    {
        ObjectNode node = JsonNodeFactory.instance.objectNode();

        node.put( GraphResult.ID_PARAM, getParentKey() );

        node.put( GraphResult.NAME_PARAM, groupEntity.getName() );

        node.put( GraphResult.DATA_PARAM,
                  createGraphData( String.valueOf( groupEntity.getGroupKey() ), groupEntity.isBuiltIn() ? "role" : "group",
                                   groupEntity.isBuiltIn(), groupEntity.getName() ) );
        node.put( GraphResult.ADJACENCIES_PARAM, createGraphAdjacencies( groupEntity.getMembers( false ) ) );
        return node;
    }


    protected ObjectNode buildGraphData( GroupEntity groupEntity )
    {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        String groupKey = getParentKey() + "_" + String.valueOf( groupEntity.getGroupKey() );
        node.put( GraphResult.ID_PARAM, groupKey );

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
        String groupKey = getParentKey() + "_" + String.valueOf( userEntity.getKey() );
        node.put( GraphResult.ID_PARAM, groupKey );

        node.put( GraphResult.NAME_PARAM, userEntity.getDisplayName() );

        node.put( GraphResult.DATA_PARAM,
                  createGraphData( String.valueOf( userEntity.getKey() ), "user", userEntity.isBuiltIn(), userEntity.getName() ) );
        return node;
    }


}

