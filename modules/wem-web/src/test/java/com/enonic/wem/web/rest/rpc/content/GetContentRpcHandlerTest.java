package com.enonic.wem.web.rest.rpc.content;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import com.enonic.wem.core.time.MockTimeService;

public class GetContentRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private MockTimeService timeService = new MockTimeService( new DateTime( 2000, 1, 1, 12, 0, 0 ) );

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final GetContentRpcHandler handler = new GetContentRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void get()
        throws Exception
    {
        final ContentData contentData = new ContentData();
        contentData.setData( EntryPath.from( "field1" ), "value1" );
        contentData.setData( EntryPath.from( "field2" ), "value2" );

        final Content content1 = Content.newContent().
            path( ContentPath.from( "/MySite/MyContent" ) ).
            createdTime( timeService.getNowAsDateTime() ).
            owner( AccountKey.user( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( timeService.getNowAsDateTime() ).
            modifier( UserKey.superUser() ).
            type( new QualifiedContentTypeName( "myModule:myType" ) ).
            data( contentData ).
            build();

        Mockito.when( client.execute( Mockito.any( Commands.content().get().getClass() ) ) ).thenReturn( Contents.from( content1 ) );

        testSuccess( "getContent_param.json", "getContent_result.json" );
    }

}