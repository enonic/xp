package com.enonic.wem.web.rest.rpc.content;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

import com.enonic.cms.core.time.MockTimeService;

import static org.mockito.Matchers.isA;

public class ListContentRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    private MockTimeService timeService = new MockTimeService( new DateTime( 2000, 1, 1, 12, 0, 0 ) );


    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final ListContentRpcHandler handler = new ListContentRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
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
        final UserKey owner = AccountKey.user( "enonic:user1" );
        final DateTime now = timeService.getNowAsDateTime();
        final String displayName = StringUtils.substringAfterLast( path, "/" ).toUpperCase();
        return Content.newContent().path( ContentPath.from( path ) ).createdTime( now ).owner( owner ).modifier(
            UserKey.superUser() ).modifiedTime( now ).displayName( displayName ).build();
    }
}
