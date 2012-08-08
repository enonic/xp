package com.enonic.wem.web.rest2.service.account;

import java.util.Collection;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.web.rest2.resource.account.graph.GraphResult;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;

public abstract class AccountGraphService
{

    private String parentKey;

    public AccountGraphService()
    {
        parentKey = "";
    }

    protected ObjectNode createGraphData( String key, String type, boolean builtIn, String name )
    {

        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        objectNode.put( GraphResult.KEY_PARAM, key );
        objectNode.put( GraphResult.TYPE_PARAM, type );
        objectNode.put( GraphResult.BUILTIN_PARAM, builtIn );
        objectNode.put( GraphResult.NAME_PARAM, name );
        return objectNode;
    }

    protected ArrayNode createGraphAdjacencies( Collection<GroupEntity> memberships )
    {
        ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        for ( GroupEntity membership : memberships )
        {
            if ( getParentKey().contains( String.valueOf( membership.getGroupKey() ) ) )
            {
                continue;
            }
            ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
            String nodeId = getMemberKey( membership );
            objectNode.put( GraphResult.NODETO_PARAM, nodeId );
            arrayNode.add( objectNode );
        }
        return arrayNode;
    }


    protected void setParentKey( String parentKey )
    {
        this.parentKey = parentKey;
    }

    protected String getParentKey()
    {
        return this.parentKey;
    }

    protected String getMemberKey( GroupEntity member )
    {
        String nodeId = getParentKey() + "_";
        if ( !member.isOfType( GroupType.USER, true ) )
        {
            nodeId += String.valueOf( member.getGroupKey() );
        }
        else
        {
            nodeId += String.valueOf( member.getUser().getKey() );
        }
        return nodeId;
    }
}
