package com.enonic.wem.admin.rpc.content;

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
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;

public class GetContentRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        DateTimeUtils.setCurrentMillisFixed( new DateTime( 2000, 1, 1, 12, 0, 0, DateTimeZone.UTC ).getMillis() );
        final GetContentRpcHandler handler = new GetContentRpcHandler();

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
    public void get_by_path()
        throws Exception
    {
        final Content myContent = createContent( "abc", "my_content" );

        Mockito.when( client.execute( Mockito.any( Commands.content().get().getClass() ) ) ).thenReturn( Contents.from( myContent ) );

        testSuccess( "getContent_byPath_param.json", "getContent_byPath_result.json" );
    }

    @Test
    public void get_by_id()
        throws Exception
    {
        final Content aaaContent = createContent( "aaa", "my_a_content" );
        final Content bbbContent = createContent( "bbb", "my_b_content" );

        Mockito.when( client.execute( Mockito.any( Commands.content().get().getClass() ) ) ).thenReturn(
            Contents.from( aaaContent, bbbContent ) );

        testSuccess( "getContent_by_contentIds_param.json", "getContent_by_contentIds_result.json" );
    }

    private Content createContent( final String id, final String name )
    {
        final ContentData contentData = new ContentData();
        contentData.setProperty( DataPath.from( "myData" ), new Value.Text( "value1" ) );

        return Content.newContent().
            id( ContentId.from( id ) ).
            path( ContentPath.from( name ) ).
            createdTime( DateTime.now() ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( DateTime.now() ).
            modifier( UserKey.superUser() ).
            type( new QualifiedContentTypeName( "mymodule:my_type" ) ).
            contentData( contentData ).
            build();
    }

}