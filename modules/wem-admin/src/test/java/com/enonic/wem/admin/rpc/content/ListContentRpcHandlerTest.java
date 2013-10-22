package com.enonic.wem.admin.rpc.content;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.AfterClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.jsonrpc.JsonRpcHandler;
import com.enonic.wem.admin.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;

import static org.mockito.Matchers.isA;

public class ListContentRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;


    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        DateTimeUtils.setCurrentMillisFixed( new DateTime( 2000, 1, 1, 12, 0, 0, DateTimeZone.UTC ).getMillis() );
        final ListContentRpcHandler handler = new ListContentRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @AfterClass
    public static void tearDown()
    {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void testListContent()
        throws Exception
    {
        final Content c1 = createContent( "a1/c1" );
        final Content c2 = createContent( "a1/c2" );
        final Content c3 = createContent( "a1/c3" );
        final Content c4 = createContent( "a1/c4" );

        final Contents contents = Contents.from( c1, c2, c3, c4 );
        Mockito.when( client.execute( isA( GetChildContent.class ) ) ).thenReturn( contents );

        testSuccess( "listContent_params.json", "listContent_result.json" );
    }

    private Content createContent( String path )
    {
        final UserKey owner = UserKey.from( "enonic:user1" );
        final DateTime now = DateTime.now();
        final String displayName = StringUtils.substringAfterLast( path, "/" ).toUpperCase();
        return Content.newContent().path( ContentPath.from( path ) ).createdTime( now ).owner( owner ).modifier(
            UserKey.superUser() ).modifiedTime( now ).displayName( displayName ).build();
    }
}
