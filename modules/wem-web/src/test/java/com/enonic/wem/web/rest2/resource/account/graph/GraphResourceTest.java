package com.enonic.wem.web.rest2.resource.account.graph;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.web.rest2.resource.AbstractResourceTest;
import com.enonic.wem.web.rest2.service.account.group.GroupGraphService;
import com.enonic.wem.web.rest2.service.account.user.UserGraphService;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

public class GraphResourceTest
    extends AbstractResourceTest
{

    private UserGraphService userGraphService;

    private GroupGraphService groupGraphService;

    private GraphResource graphResource;

    private UserDao userDao;

    private GroupDao groupDao;

    @Before
    public void setup()
    {
        graphResource = new GraphResource();
        userGraphService = Mockito.mock( UserGraphService.class );
        groupGraphService = Mockito.mock( GroupGraphService.class );
        userDao = Mockito.mock( UserDao.class );
        groupDao = Mockito.mock( GroupDao.class );
        graphResource.setGroupDao( groupDao );
        graphResource.setUserDao( userDao );
        graphResource.setGroupGraphService( groupGraphService );
        graphResource.setUserGraphService( userGraphService );
    }

    @Test
    public void testBuildUserGraph()
        throws Exception
    {
        UserEntity user = createUser( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" );
        Mockito.when( userDao.findByKey( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" ) ).thenReturn( user );
        Mockito.when( userGraphService.generateGraph( user ) ).thenReturn( generateUserGraph() );

        GraphResult result = graphResource.getInfo( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" );
        assertJsonResult( "user_graph_info.json", result );
    }

    @Test
    public void testBuildGroupGraph()
        throws Exception
    {
        GroupEntity group = createGroup( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" );
        Mockito.when( groupDao.findByKey( new GroupKey( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" ) ) ).thenReturn( group );
        Mockito.when( groupGraphService.generateGraph( group ) ).thenReturn( generateGroupGraph() );

        GraphResult result = graphResource.getInfo( "ASDD8F7S9F9AFAF7A89F7A87F98A7F9A87FA89F79AS98G7A9" );
        assertJsonResult( "group_graph_info.json", result );
    }

    private UserEntity createUser( final String key )
        throws Exception
    {
        UserEntity user = new UserEntity();

        user.setKey( new UserKey( key ) );
        user.setType( UserType.NORMAL );
        user.setEmail( "user@email.com" );
        user.setUserStore( createUserstore( "enonic" ) );
        user.setName( "dummy" );
        user.setDisplayName( "Dummy User" );

        return user;
    }

    private GroupEntity createGroup( final String key )
    {
        GroupEntity group = new GroupEntity();
        group.setKey( new GroupKey( key ) );
        group.setType( GroupType.USERSTORE_GROUP );
        group.setUserStore( createUserstore( "enonic" ) );
        group.setName( "group1" );
        group.setDescription( "Group One" );
        return group;
    }

    private UserStoreEntity createUserstore( final String name )
    {
        UserStoreEntity userstore = new UserStoreEntity();
        userstore.setName( name );
        return userstore;
    }

    private GraphResult generateUserGraph()
    {
        GraphResult root = new GraphResult();
        ArrayNode graph = (ArrayNode) root.toJson().get( GraphResult.GRAPH_PARAM );

        ObjectNode userNode = JsonNodeFactory.instance.objectNode();
        userNode.put( GraphResult.ID_PARAM, "1343213539380_E34B614B26C666AA9929F90EF3FA723B3DAAAAB2" );
        userNode.put( GraphResult.NAME_PARAM, "John Doe" );

        ObjectNode userData = createGraphData( false, "user", "E34B614B26C666AA9929F90EF3FA723B3DAAAAB2", "jdoe" );
        userNode.put( GraphResult.DATA_PARAM, userData );

        ArrayNode adjacencies = JsonNodeFactory.instance.arrayNode();
        ObjectNode adjacency = JsonNodeFactory.instance.objectNode();
        adjacency.put( GraphResult.NODETO_PARAM,
                       "1343213539380_E34B614B26C666AA9929F90EF3FA723B3DAAAAB2_4DCC2040F37F8129E31231268691DBD88473747C" );
        adjacencies.add( adjacency );
        userNode.put( GraphResult.ADJACENCIES_PARAM, adjacencies );

        graph.add( userNode );

        ObjectNode groupNode = JsonNodeFactory.instance.objectNode();
        groupNode.put( GraphResult.ID_PARAM,
                       "1343213539380_E34B614B26C666AA9929F90EF3FA723B3DAAAAB2_4DCC2040F37F8129E31231268691DBD88473747C" );
        groupNode.put( GraphResult.NAME_PARAM, "Togservice" );

        ObjectNode groupData = createGraphData( false, "group", "4DCC2040F37F8129E31231268691DBD88473747C", "Togservice" );
        groupNode.put( GraphResult.DATA_PARAM, groupData );

        graph.add( groupNode );

        return root;
    }

    private GraphResult generateGroupGraph()
    {
        GraphResult root = new GraphResult();
        ArrayNode graph = (ArrayNode) root.toJson().get( GraphResult.GRAPH_PARAM );

        ObjectNode groupNode = JsonNodeFactory.instance.objectNode();
        groupNode.put( GraphResult.ID_PARAM, "1343213539380_E34B614B26C666AA9929F90EF3FA723B3DAAAAB2" );
        groupNode.put( GraphResult.NAME_PARAM, "Togservice" );

        ObjectNode groupData = createGraphData( false, "group", "E34B614B26C666AA9929F90EF3FA723B3DAAAAB2", "Togservice" );
        groupNode.put( GraphResult.DATA_PARAM, groupData );

        ArrayNode adjacencies = JsonNodeFactory.instance.arrayNode();
        ObjectNode adjacency = JsonNodeFactory.instance.objectNode();
        adjacency.put( GraphResult.NODETO_PARAM,
                       "1343213539380_E34B614B26C666AA9929F90EF3FA723B3DAAAAB2_4DCC2040F37F8129E31231268691DBD88473747C" );
        adjacencies.add( adjacency );
        groupNode.put( GraphResult.ADJACENCIES_PARAM, adjacencies );

        graph.add( groupNode );

        ObjectNode userNode = JsonNodeFactory.instance.objectNode();
        userNode.put( GraphResult.ID_PARAM,
                      "1343213539380_E34B614B26C666AA9929F90EF3FA723B3DAAAAB2_4DCC2040F37F8129E31231268691DBD88473747C" );
        userNode.put( GraphResult.NAME_PARAM, "Magne Magnesson" );

        ObjectNode userData = createGraphData( false, "user", "4DCC2040F37F8129E31231268691DBD88473747C", "magne" );
        userNode.put( GraphResult.DATA_PARAM, userData );

        graph.add( userNode );

        return root;
    }

    private ObjectNode createGraphData( boolean builtin, String type, String key, String name )
    {
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put( GraphResult.BUILTIN_PARAM, builtin );
        data.put( GraphResult.TYPE_PARAM, type );
        data.put( GraphResult.KEY_PARAM, key );
        data.put( GraphResult.NAME_PARAM, name );
        return data;
    }
}
