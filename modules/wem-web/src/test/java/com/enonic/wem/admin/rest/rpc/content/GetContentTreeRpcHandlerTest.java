package com.enonic.wem.admin.rest.rpc.content;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.json.rpc.JsonRpcHandler;
import com.enonic.wem.admin.rest.rpc.AbstractRpcHandlerTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.GetContentTree;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;

public class GetContentTreeRpcHandlerTest
    extends AbstractRpcHandlerTest
{

    private Client client;

    @Override
    protected JsonRpcHandler createHandler()
        throws Exception
    {
        DateTimeUtils.setCurrentMillisFixed( new DateTime( 2000, 1, 1, 12, 0, 0, DateTimeZone.UTC ).getMillis() );
        final GetContentTreeRpcHandler handler = new GetContentTreeRpcHandler();

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
    public void getContentTree()
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

    @Ignore // Until fixed, RMY
    @Test
    public void getContentTreeWithTopLevelNodes()
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

        final GetContentTree command = Commands.content().getTree();
        command.selectors( ContentIds.from( "c1" ) );

        Mockito.when( client.execute( command ) ).thenReturn( contentTree );

        testSuccess( "getContentTree_specify_top_level_nodes_param.json", "getContentTree_specify_top_level_nodes_result.json" );

        Mockito.verify( client, Mockito.times( 1 ) ).execute( command );
    }


    private Content createContent( String path )
    {
        return Content.newContent().path( ContentPath.from( path ) ).createdTime( DateTime.now() ).owner(
            UserKey.from( "myStore:me" ) ).build();
    }
}