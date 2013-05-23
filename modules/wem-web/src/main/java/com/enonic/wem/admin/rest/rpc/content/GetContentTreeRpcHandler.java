package com.enonic.wem.admin.rest.rpc.content;


import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.GetContentTree;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.support.tree.Tree;


public final class GetContentTreeRpcHandler
    extends AbstractDataRpcHandler
{
    public GetContentTreeRpcHandler()
    {
        super( "content_tree" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final GetContentTree getContentTree = Commands.content().getTree();

        if ( context.hasParam( "contentIds" ) )
        {
            final String[] contentIds = context.param( "contentIds" ).asStringArray();
            getContentTree.selectors( ContentIds.from( contentIds ) );
        }

        final Tree<Content> contentTree = client.execute( getContentTree );
        context.setResult( new ContentTreeJsonResult( contentTree ) );
    }
}
