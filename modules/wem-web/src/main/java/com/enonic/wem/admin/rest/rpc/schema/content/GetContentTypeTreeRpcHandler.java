package com.enonic.wem.admin.rest.rpc.schema.content;


import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.support.tree.Tree;

import static com.enonic.wem.api.command.Commands.contentType;


public final class GetContentTypeTreeRpcHandler
    extends AbstractDataRpcHandler
{
    public GetContentTypeTreeRpcHandler()
    {
        super( "contentType_tree" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final Tree<ContentType> contentTypeTree = client.execute( contentType().getTree() );
        context.setResult( new GetContentTypeTreeJsonResult( contentTypeTree ) );
    }
}
