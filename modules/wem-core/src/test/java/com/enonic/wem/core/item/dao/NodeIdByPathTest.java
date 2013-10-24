package com.enonic.wem.core.item.dao;


import java.util.LinkedHashMap;

import org.junit.Test;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NodePath;

import static junit.framework.Assert.assertNotNull;

public class NodeIdByPathTest
{
    private NodeIdByPath nodeIdByPath = new NodeIdByPath( new LinkedHashMap<NodePath, EntityId>() );

    @Test
    public void when_get_given_a_path_with_associated_ItemId_then_notNull_is_returned()
    {
        nodeIdByPath.put( new NodePath( "/path" ), new EntityId() );
        assertNotNull( nodeIdByPath.get( new NodePath( "/path" ) ) );
    }
}
