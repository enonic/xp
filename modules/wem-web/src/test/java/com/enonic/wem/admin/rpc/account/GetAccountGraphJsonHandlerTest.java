package com.enonic.wem.admin.rpc.account;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Commands;

import static org.junit.Assert.*;

public class GetAccountGraphJsonHandlerTest
    extends AbstractAccountRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        client = Mockito.mock( Client.class );
        GetAccountGraphRpcHandler handler = new GetAccountGraphRpcHandler();
        handler.setClient( client );
        return handler;
    }

    @Test
    public void testGetUserGraph()
        throws Exception
    {
        AccountKey userKey = AccountKey.from( "user:enonic:aro" );
        AccountKey groupKey = AccountKey.from( "group:enonic:Togservice" );
        Mockito.when( client.execute( Commands.account().findMemberships().key( userKey ) ) ).thenReturn( AccountKeys.from( groupKey ) );
        Mockito.when( client.execute( Commands.account().findMemberships().key( groupKey ) ) ).thenReturn( AccountKeys.empty() );
        Mockito.when( client.execute( Commands.account().get().keys( AccountKeys.from( userKey, groupKey ) ).includeImage() ) ).thenReturn(
            createAccountsObject( createUser( "enonic:aro" ), createGroup( "enonic:Togservice" ) ) );
        testGraphSuccess( "getUserGraph_param.json", "getUserGraph_result.json" );
    }


    @Test
    public void testGetGroupGraph()
        throws Exception
    {
        AccountKey userKey = AccountKey.from( "user:enonic:aro" );
        AccountKey groupKey = AccountKey.from( "group:enonic:Togservice" );
        Mockito.when( client.execute( Commands.account().findMembers().key( groupKey ) ) ).thenReturn( AccountKeys.from( userKey ) );
        Mockito.when( client.execute( Commands.account().get().keys( AccountKeys.from( userKey, groupKey ) ).includeImage() ) ).thenReturn(
            createAccountsObject( createUser( "enonic:aro" ), createGroup( "enonic:Togservice" ) ) );
        testGraphSuccess( "getGroupGraph_param.json", "getGroupGraph_result.json" );
    }

    @Test
    public void testGetRoleGraph()
        throws Exception
    {
        AccountKey userKey = AccountKey.from( "user:enonic:aro" );
        AccountKey groupKey = AccountKey.from( "group:enonic:Togservice" );
        AccountKey roleKey = AccountKey.from( "role:enonic:admin" );
        Mockito.when( client.execute( Commands.account().findMembers().key( roleKey ) ) ).thenReturn(
            AccountKeys.from( userKey, groupKey ) );
        Mockito.when( client.execute( Commands.account().findMembers().key( groupKey ) ) ).thenReturn( AccountKeys.empty() );
        Mockito.when(
            client.execute( Commands.account().get().keys( AccountKeys.from( userKey, groupKey, roleKey ) ).includeImage() ) ).thenReturn(
            createAccountsObject( createRole( "enonic:admin" ), createUser( "enonic:aro" ), createGroup( "enonic:Togservice" ) ) );
        testGraphSuccess( "getRoleGraph_param.json", "getRoleGraph_result.json" );
    }

    private void assertGraphJson( JsonNode expected, JsonNode actual )
    {
        ArrayNode expectedGraph = (ArrayNode) expected.get( "graph" );
        ArrayNode actualGraph = (ArrayNode) actual.get( "graph" );
        assertEquals( expectedGraph.size(), actualGraph.size() );
        assertEquals( createAdjacenciesMap( expectedGraph ), createAdjacenciesMap( actualGraph ) );
        assertGraphData( expectedGraph, actualGraph );
    }

    private void assertGraphStructure( JsonNode graphResponse )
    {
        assertTrue( "Required field graph is missing", graphResponse.has( "graph" ) );
        ArrayNode graph = (ArrayNode) graphResponse.get( "graph" );
        Iterator<JsonNode> graphIterator = graph.iterator();
        while ( graphIterator.hasNext() )
        {
            JsonNode graphNode = graphIterator.next();
            assertTrue( "Required field id is missing", graphNode.has( "id" ) );
            assertTrue( "Required field name is missing", graphNode.has( "name" ) );
            assertTrue( "Required field data is missing", graphNode.has( "data" ) );
            assertTrue( "Required field adjacencies is missing", graphNode.has( "adjacencies" ) );
            // Test data node
            JsonNode dataNode = graphNode.get( "data" );
            assertTrue( "Required field type in data is missing", dataNode.has( "type" ) );
            assertTrue( "Required field key in data is missing", dataNode.has( "key" ) );
            assertTrue( "Required field name in data is missing", dataNode.has( "name" ) );
            assertTrue( "Required field image_uri in data is missing", dataNode.has( "image_uri" ) );
            // Test adjacencies node
            ArrayNode adjacenciesNode = (ArrayNode) graphNode.get( "adjacencies" );
            Iterator<JsonNode> adjacenciesIterator = adjacenciesNode.iterator();
            while ( adjacenciesIterator.hasNext() )
            {
                assertTrue( "Required field nodeTo in adjacencies is missing", adjacenciesIterator.next().has( "nodeTo" ) );
            }
        }
    }

    private Map<String, Set<String>> createAdjacenciesMap( ArrayNode graph )
    {
        Map<String, Set<String>> adjacenciesMap = new HashMap<String, Set<String>>();
        for ( JsonNode graphNode : graph )
        {
            String id = graphNode.get( "id" ).asText();
            ArrayNode adjacenciesNode = (ArrayNode) graphNode.get( "adjacencies" );
            Set<String> adjacencies = new HashSet<String>();
            for ( JsonNode nodeTo : adjacenciesNode )
            {
                adjacencies.add( extractKey( nodeTo.get( "nodeTo" ).asText() ) );
            }
            adjacenciesMap.put( extractKey( id ), adjacencies );
        }
        return adjacenciesMap;
    }

    private void assertGraphData( ArrayNode expected, ArrayNode actual )
    {
        for ( JsonNode node : expected )
        {
            String id = extractKey( node.get( "id" ).asText() );
            JsonNode matchedNode = findGraphNodeById( id, actual );
            assertNotNull( "Expected graph node with id: " + id + "is not found", matchedNode );
            assertGraphNode( node, matchedNode );
        }
    }

    private void assertGraphNode( JsonNode expected, JsonNode actual )
    {
        Iterator<String> fieldIterator = expected.fieldNames();
        while ( fieldIterator.hasNext() )
        {
            String fieldName = fieldIterator.next();
            if ( !fieldName.equals( "id" ) && !fieldName.equals( "adjacencies" ) && !fieldName.equals( "data" ) )
            {
                assertEquals( "Values of the field '" + fieldName + "' doesn't match", expected.get( fieldName ), actual.get( fieldName ) );
            }
            else if ( fieldName.equals( "data" ) )
            {
                JsonNode expectedDataNode = expected.get( fieldName );
                JsonNode actualDataNode = actual.get( fieldName );
                assertGraphNode( expectedDataNode, actualDataNode );
            }
        }
    }

    private JsonNode findGraphNodeById( String id, ArrayNode graph )
    {
        for ( JsonNode node : graph )
        {
            String nodeId = extractKey( node.get( "id" ).asText() );
            if ( id.equals( nodeId ) )
            {
                return node;
            }
        }
        return null;
    }

    private String extractKey( String id )
    {
        return id.replaceFirst( "^\\d+_", "" );
    }

    private void testGraphSuccess( final String paramsFile, final String resultFile )
        throws Exception
    {
        final JsonNode paramsJson = parseJson( paramsFile );
        final JsonNode resultJson = parseJson( resultFile );
        assertNotNull( paramsJson );
        assertTrue( paramsJson instanceof ObjectNode );
        JsonNode actual = getJsonResult( paramsJson );
        assertGraphStructure( actual );
        assertGraphJson( resultJson, actual );
    }


}
