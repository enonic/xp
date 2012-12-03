package com.enonic.wem.web.rest.rpc.content.type;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.type.ContentTypeTree;
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
        final ContentTypeTree contentTypesTree = client.execute( contentType().getTree() );
        context.setResult( new GetContentTypeTreeJsonResult( contentTypesTree ) );
    }
}
