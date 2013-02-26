package com.enonic.wem.web.rest.rpc.content;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.AfterClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

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
    public void get()
        throws Exception
    {
        final RootDataSet rootDataSet = DataSet.newRootDataSet();
        rootDataSet.setData( EntryPath.from( "field1" ), "value1" );
        rootDataSet.setData( EntryPath.from( "field2" ), "value2" );

        final Content content1 = Content.newContent().
            path( ContentPath.from( "/MySite/MyContent" ) ).
            createdTime( DateTime.now() ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( DateTime.now() ).
            modifier( UserKey.superUser() ).
            type( new QualifiedContentTypeName( "myModule:myType" ) ).
            rootDataSet( rootDataSet ).
            build();

        Mockito.when( client.execute( Mockito.any( Commands.content().get().getClass() ) ) ).thenReturn( Contents.from( content1 ) );

        testSuccess( "getContent_param.json", "getContent_result.json" );
    }

}