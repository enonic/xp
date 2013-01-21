package com.enonic.wem.web.rest.rpc.content;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.GetContentTree;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
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
        final Tree<Content> contentTree = client.execute( getContentTree );
        context.setResult( new ContentTreeJsonResult( contentTree ) );
    }
}
