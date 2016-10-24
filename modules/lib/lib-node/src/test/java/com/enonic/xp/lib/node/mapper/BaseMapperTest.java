package com.enonic.xp.lib.node.mapper;

import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.script.serializer.JsonMapGenerator;

import static org.junit.Assert.*;

public abstract class BaseMapperTest
{
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
        final JsonNode expectedNode = parseJson( readFromFile( fileName ) );

        assertEquals( expectedNode, actualNode.getRoot() );
    }

    private JsonNode parseJson( final String json )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperHelper.create();
        return mapper.readTree( json );
    }

    private String readFromFile( final String fileName )
        throws Exception
    {
        final URL url = getClass().getResource( fileName );
        if ( url == null )
        {
            throw new IllegalArgumentException( "Resource file [" + fileName + "] not found" );
        }

        return Resources.toString( url, Charsets.UTF_8 );
    }

}
