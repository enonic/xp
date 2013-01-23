package com.enonic.wem.web.rest.rpc.content;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;
import com.enonic.wem.core.time.MockTimeService;
import com.enonic.wem.web.json.rpc.JsonRpcHandler;
import com.enonic.wem.web.rest.rpc.AbstractRpcHandlerTest;

public class GetContentTreeRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private MockTimeService timeService = new MockTimeService( new DateTime( 2000, 1, 1, 12, 0, 0 ) );

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        final GetContentTreeRpcHandler handler = new GetContentTreeRpcHandler();

        client = Mockito.mock( Client.class );
        handler.setClient( client );

        return handler;
    }

    @Test
    public void get()
        throws Exception
    {
        Content c1 = createContent( "c1" );
        Content c2 = createContent( "c1/c2" );
        Content c3 = createContent( "c1/c2/c3" );
        Content c4 = createContent( "c1/c4" );

        Tree<Content> contentTree = new Tree<Content>();
        TreeNode<Content> node_c1 = contentTree.createNode( c1 );
        node_c1.addChild( c2 ).addChild( c3 );
        node_c1.addChild( c4 );

        Mockito.when( client.execute( Mockito.any( Commands.content().getTree().getClass() ) ) ).thenReturn( contentTree );

        testSuccess( "getContentTree_param.json", "getContentTree_result.json" );
    }

    private Content createContent( String path )
    {
        return Content.newContent().path( ContentPath.from( path ) ).createdTime( timeService.getNowAsDateTime() ).owner(
            AccountKey.user( "myStore:me" ) ).build();
    }
}