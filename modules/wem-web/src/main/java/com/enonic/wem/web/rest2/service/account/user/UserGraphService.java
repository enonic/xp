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

    public GraphResult buildGraph( UserEntity entity )
    {
        setParentKey( System.currentTimeMillis() + "_" + String.valueOf( entity.getKey() ) );
        GraphResult userResult = new GraphResult();
        userResult.addAccountNode( buildGraphData( entity ) );

        for ( GroupEntity group : entity.getDirectMemberships() )
        {
            ObjectNode node = buildGraphData( group );
            userResult.addAccountNode( node );
        }

        return userResult;
    }

    protected ObjectNode buildGraphData( UserEntity userEntity )
    {
        ObjectNode node = super.buildGraphData( userEntity );
        node.put( GraphResult.ID_PARAM, getParentKey() );
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
