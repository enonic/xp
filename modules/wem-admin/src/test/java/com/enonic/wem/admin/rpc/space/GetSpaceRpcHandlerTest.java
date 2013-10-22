package com.enonic.wem.admin.rpc.space;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.space.GetSpaces;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.api.space.Spaces;

import static org.mockito.Matchers.isA;

public class GetSpaceRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private Client client;

    private static final DateTime CURRENT_TIME = new DateTime( 2000, 1, 1, 12, 0, 0, DateTimeZone.UTC );

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final GetSpaceRpcHandler handler = new GetSpaceRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void get_single()
        throws Exception
    {
        final Space space = Space.newSpace().
            name( SpaceName.from( "mySpace" ) ).
            displayName( "My Space" ).
            modifiedTime( CURRENT_TIME ).
            createdTime( CURRENT_TIME ).
            rootContent( ContentId.from( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            build();

        Mockito.when( client.execute( isA( GetSpaces.class ) ) ).thenReturn( Spaces.from( space ) );

        final ObjectNode params = objectNode();
        params.put( "spaceNames", "mySpace" );
        testSuccess( params, "getSpace_result.json" );
    }

    @Test
    public void get_multiple()
        throws Exception
    {
        final Space space1 = Space.newSpace().
            name( SpaceName.from( "mySpace" ) ).
            displayName( "My Space" ).
            modifiedTime( CURRENT_TIME ).
            createdTime( CURRENT_TIME ).
            rootContent( ContentId.from( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            build();
        final Space space2 = Space.newSpace().
            name( SpaceName.from( "mySpace2" ) ).
            displayName( "My Space2" ).
            modifiedTime( CURRENT_TIME ).
            createdTime( CURRENT_TIME ).
            rootContent( ContentId.from( "4d99aa26-2b08-4a52-939c-8f44e3f400cc" ) ).
            build();
        final Space space3 = Space.newSpace().
            name( SpaceName.from( "mySpace3" ) ).
            displayName( "My Space3" ).
            modifiedTime( CURRENT_TIME ).
            createdTime( CURRENT_TIME ).
            rootContent( ContentId.from( "e55380e3-8405-4027-8c08-98f71952b22f" ) ).
            build();

        Mockito.when( client.execute( isA( GetSpaces.class ) ) ).thenReturn( Spaces.from( space1, space2, space3 ) );

        final ObjectNode params = objectNode();
        final ArrayNode spaceNamesParam = params.putArray( "spaceNames" );
        spaceNamesParam.add( "mySpace" );
        spaceNamesParam.add( "mySpace2" );
        spaceNamesParam.add( "mySpace3" );
        testSuccess( params, "getSpace_result_multiple.json" );
    }

    @Test
    public void get_not_found()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetSpaces.class ) ) ).thenReturn( Spaces.empty() );

        final ObjectNode params = objectNode();
        params.put( "spaceNames", "mySpace" );
        testSuccess( params, "getSpace_result_not_found.json" );
    }

    @Test
    public void get_partially_not_found()
        throws Exception
    {
        final Space space = Space.newSpace().
            name( SpaceName.from( "mySpace" ) ).
            displayName( "My Space" ).
            modifiedTime( CURRENT_TIME ).
            createdTime( CURRENT_TIME ).
            rootContent( ContentId.from( "1fad493a-6a72-41a3-bac4-88aba3d83bcc" ) ).
            build();

        Mockito.when( client.execute( isA( GetSpaces.class ) ) ).thenReturn( Spaces.from( space ) );

        final ObjectNode params = objectNode();
        params.put( "spaceNames", "mySpace" );
        final ArrayNode spaceNamesParam = params.putArray( "spaceNames" );
        spaceNamesParam.add( "mySpace" );
        spaceNamesParam.add( "mySpace2" );
        spaceNamesParam.add( "mySpace3" );
        testSuccess( params, "getSpace_result_some_not_found.json" );
    }
}