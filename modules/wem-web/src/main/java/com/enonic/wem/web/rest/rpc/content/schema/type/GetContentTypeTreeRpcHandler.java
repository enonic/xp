package com.enonic.wem.web.rest.rpc.content.schema.type;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.schema.type.ContentType;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.command.Commands.contentType;

@Component
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
