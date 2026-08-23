package com.enonic.xp.lib.node.mapper;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.testing.serializer.JsonMapGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NodeMapperTest
{
    @Test
    void order_key_serialized()
    {
        final Node node =
            Node.create().name( "my-node" ).parentPath( NodePath.ROOT ).orderKey( "3ala4x8dnbc.my-node-id" ).build();

        assertEquals( "3ala4x8dnbc.my-node-id", serialize( node ).get( "_orderKey" ).textValue() );
    }

    @Test
    void order_key_null_when_node_has_none()
    {
        final Node node = Node.create().name( "my-node" ).parentPath( NodePath.ROOT ).build();

        assertTrue( serialize( node ).get( "_orderKey" ).isNull() );
    }

    private static JsonNode serialize( final Node node )
    {
        final JsonMapGenerator gen = new JsonMapGenerator();
        new NodeMapper( node ).serialize( gen );
        return (JsonNode) gen.getRoot();
    }
}
