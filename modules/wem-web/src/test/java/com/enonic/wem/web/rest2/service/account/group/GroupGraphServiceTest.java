package com.enonic.wem.web.rest2.service.account.group;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.web.rest2.resource.account.graph.GraphResult;
import com.enonic.wem.web.rest2.service.account.AccountGraphServiceTest;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;

import static org.junit.Assert.*;

public class GroupGraphServiceTest
    extends AccountGraphServiceTest
{

    private GroupGraphService groupGraphService;

    @Before
    public void setUp()
    {
        groupGraphService = new GroupGraphService();
    }

    @Test
    public void testBuildGraph()
    {
        GraphResult group = groupGraphService.buildGraph( createTestGroupGraph() );

        assertNotNull( group.toJson().get( GraphResult.GRAPH_PARAM ) );
        ArrayNode graph = (ArrayNode) group.toJson().get( GraphResult.GRAPH_PARAM );

        assertEquals( graph.size(), 7 );

        ObjectNode parentNode = (ObjectNode) graph.get( 0 );

        assertNotNull( parentNode.get( GraphResult.ADJACENCIES_PARAM ) );
        ArrayNode adjacencies = (ArrayNode) parentNode.get( GraphResult.ADJACENCIES_PARAM );

        assertEquals( adjacencies.size(), 6 );
        assertEquals( parentNode.get( GraphResult.NAME_PARAM ).getTextValue(), "parentgroup" );
    }

    @Test
    public void testGraphAdjacencies()
    {
        GraphResult group = groupGraphService.buildGraph( createTestGroupGraph() );
        assertNotNull( group.toJson().get( GraphResult.GRAPH_PARAM ) );
        ArrayNode graph = (ArrayNode) group.toJson().get( GraphResult.GRAPH_PARAM );
        ObjectNode parentNode = (ObjectNode) graph.get( 0 );
        assertNotNull( parentNode.get( GraphResult.ADJACENCIES_PARAM ) );
        ArrayNode adjacencies = (ArrayNode) parentNode.get( GraphResult.ADJACENCIES_PARAM );
        Iterator<JsonNode> elements = adjacencies.getElements();
        while ( elements.hasNext() )
        {
            JsonNode node = elements.next();
            assertNotNull( node.get( GraphResult.NODETO_PARAM ) );
            String node_to = node.get( GraphResult.NODETO_PARAM ).getTextValue();
            JsonNode adjacentNode = findNodeById( graph, node_to );
            assertNotNull( adjacentNode );
        }
    }

    @Test
    public void testDataSection()
    {
        GraphResult group = groupGraphService.buildGraph( createTestGroupGraph() );
        assertNotNull( group.toJson().get( GraphResult.GRAPH_PARAM ) );
        System.out.println( group.toJson() );
        ArrayNode graph = (ArrayNode) group.toJson().get( GraphResult.GRAPH_PARAM );
        Iterator<JsonNode> elements = graph.getElements();
        assert ( elements.hasNext() );
        JsonNode current = elements.next();
        //Test parent node data section
        assertNotNull( current.get( GraphResult.DATA_PARAM ) );
        JsonNode currentData = current.get( GraphResult.DATA_PARAM );
        assertEquals( currentData.get( GraphResult.NAME_PARAM ).getTextValue(), "parentgroup" );
        assertEquals( currentData.get( GraphResult.TYPE_PARAM ).getTextValue(), "group" );
        assertEquals( currentData.get( GraphResult.BUILTIN_PARAM ).asBoolean(), false );
        assertNotNull( currentData.get( GraphResult.KEY_PARAM ) );

        // Test rest nodes
        while ( elements.hasNext() )
        {
            current = elements.next();
            currentData = current.get( GraphResult.DATA_PARAM );
            assertEquals( currentData.get( GraphResult.NAME_PARAM ).getTextValue(), "group1" );
            assertEquals( currentData.get( GraphResult.TYPE_PARAM ).getTextValue(), "group" );
            assertEquals( currentData.get( GraphResult.BUILTIN_PARAM ).getBooleanValue(), false );
            assertNotNull( currentData.get( GraphResult.KEY_PARAM ) );
        }
    }

    private JsonNode findNodeById( ArrayNode graph, String id )
    {
        Iterator<JsonNode> elements = graph.getElements();
        while ( elements.hasNext() )
        {
            JsonNode node = elements.next();
            if ( node.get( GraphResult.ID_PARAM ).getTextValue().equals( id ) )
            {
                return node;
            }
        }
        return null;
    }

    private GroupEntity createTestGroupGraph()
    {
        GroupEntity group = Mockito.mock( GroupEntity.class );
        Mockito.when( group.getGroupKey() ).thenReturn( new GroupKey( "5245G2H5G23G5JHF5GH2F52GJ5F" ) );
        Mockito.when( group.getType() ).thenReturn( GroupType.USERSTORE_GROUP );
        Mockito.when( group.getUserStore() ).thenReturn( createUserstore( "enonic" ) );
        Mockito.when( group.getName() ).thenReturn( "parentgroup" );
        Mockito.when( group.getDescription() ).thenReturn( "Parent Group" );
        Mockito.when( group.getAllMembersRecursively() ).thenReturn( createGroupMembers() );
        Mockito.when( group.getMembers( false ) ).thenReturn( createGroupMembers() );

        return group;
    }

    private Set<GroupEntity> createGroupMembers()
    {
        Set<GroupEntity> members = new HashSet<GroupEntity>();
        members.add( createGroup( "FJADKLFJAKDF4K23J54KL23J5KL2J5L2K" ) );
        members.add( createGroup( "J76KL4J54LD7J54LK7J45KL7JL54K7J54" ) );
        members.add( createGroup( "5H34K5H3K5H3KL5HL3K5HLK35H3L34KLJ" ) );
        members.add( createGroup( "56357H3L57H3GF464JG34F6J3F6J3GF6J" ) );
        members.add( createGroup( "F2DF6JH34F62JH6JH3F6J3HF6HJ32F6JJ" ) );
        members.add( createGroup( "GHF643HFJ32K42GF35G6DFG42362K345F" ) );
        return members;
    }
}
