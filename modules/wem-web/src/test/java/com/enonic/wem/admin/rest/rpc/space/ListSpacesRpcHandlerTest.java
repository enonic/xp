package com.enonic.wem.admin.rest.rpc.space;

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

public class ListSpacesRpcHandlerTest
    extends AbstractRpcHandlerTest
{
    private Client client;

    private static final DateTime CURRENT_TIME = new DateTime( 2000, 1, 1, 12, 0, 0, DateTimeZone.UTC );

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final ListSpacesRpcHandler handler = new ListSpacesRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void testListSpaces()
        throws Exception
    {
        final Space s1 = createSpace( "space1", "My space 1" );
        final Space s2 = createSpace( "space2", "My space 2" );
        final Space s3 = createSpace( "space3", "My space 3" );
        final Space s4 = createSpace( "space4", "My space 4" );

        final Spaces spaces = Spaces.from( s1, s2, s3, s4 );
        Mockito.when( client.execute( isA( GetSpaces.class ) ) ).thenReturn( spaces );

        testSuccess( "listSpaces_result.json" );
    }

    private Space createSpace( final String name, final String displayName )
    {
        return Space.newSpace().
            name( SpaceName.from( name ) ).
            displayName( displayName ).
            modifiedTime( CURRENT_TIME ).
            createdTime( CURRENT_TIME ).
            rootContent( ContentId.from( Integer.toHexString( name.hashCode() ) ) ).
            build();
    }
}
