package com.enonic.xp.lib.node.mapper;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.script.serializer.JsonMapGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseMapperTest
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    NodeBranchEntry createEntry( final String a )
    {
        return NodeBranchEntry.create().
            nodeId( NodeId.from( a ) ).
            nodePath( NodePath.create( a ).build() ).
            nodeState( NodeState.DEFAULT ).
            build();
    }


    void assertJson( final String fileName, final JsonMapGenerator actualNode )
        throws Exception
    {
        final JsonNode expectedNode = MAPPER.readTree( readFromFile( fileName ) );

        assertEquals( expectedNode, actualNode.getRoot() );
    }

    private String readFromFile( final String fileName )
        throws Exception
    {
        final InputStream stream =
            Objects.requireNonNull( getClass().getResourceAsStream( fileName ), "Resource file [" + fileName + "] not found" );
        try (stream)
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
    }
}
