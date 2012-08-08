package com.enonic.wem.web.rest2.service.account.user;

import java.io.IOException;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.web.rest2.resource.account.graph.GraphResult;
import com.enonic.wem.web.rest2.service.account.AccountGraphServiceTest;

import static org.junit.Assert.*;

public class UserGraphServiceTest
    extends AccountGraphServiceTest
{

    private UserGraphService userGraphService;

    @Before
    public void setUp()
    {
        userGraphService = new UserGraphService();
    }

    @Test
    public void testBuildGraph()
        throws IOException
    {
        final String userKey = "J346JK346H2G7UI3G73J573L73KLG6735KL7G3L7G35L";
        GraphResult result = userGraphService.buildGraph( createUser( userKey ) );
        assertNotNull( result.toJson().get( GraphResult.GRAPH_PARAM ) );

        ArrayNode graph = (ArrayNode) result.toJson().get( GraphResult.GRAPH_PARAM );
        assertEquals( graph.size(), 2 );

        ObjectNode user = (ObjectNode) graph.get( 0 );
        ObjectNode group = (ObjectNode) graph.get( 1 );

        assertEquals( user.get( GraphResult.NAME_PARAM ).getTextValue(), "Dummy User" );

        assertNotNull( user.get( GraphResult.DATA_PARAM ) );
        ObjectNode data = (ObjectNode) user.get( GraphResult.DATA_PARAM );
        assertEquals( data.get( GraphResult.KEY_PARAM ).getTextValue(), userKey );

        assertNotNull( user.get( "adjacencies" ) );
        ArrayNode adjacencies = (ArrayNode) user.get( "adjacencies" );
        assertEquals( adjacencies.size(), 1 );

        assertEquals( group.get( GraphResult.NAME_PARAM ).getTextValue(), "group1" );
        assertNotNull( group.get( GraphResult.DATA_PARAM ) );
        ObjectNode groupData = (ObjectNode) group.get( GraphResult.DATA_PARAM );
        assertEquals( groupData.get( GraphResult.KEY_PARAM ).getTextValue(), "AC16A0357BA5632DF513C96687B287C1B97B2C78" );
    }


}
