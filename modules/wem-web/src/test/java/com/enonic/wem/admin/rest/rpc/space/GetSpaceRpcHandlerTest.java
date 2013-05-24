package com.enonic.wem.admin.rest.rpc.space;

import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.json.rpc.JsonRpcHandler;
import com.enonic.wem.admin.rest.rpc.AbstractRpcHandlerTest;
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
    public void get()
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
        params.put( "spaceName", "mySpace" );
        testSuccess( params, "getSpace_result.json" );
    }

    @Test
    public void get_not_found()
        throws Exception
    {
        Mockito.when( client.execute( isA( GetSpaces.class ) ) ).thenReturn( Spaces.empty() );

        final ObjectNode params = objectNode();
        params.put( "spaceName", "mySpace" );
        testSuccess( params, "getSpace_result_not_found.json" );
    }
}