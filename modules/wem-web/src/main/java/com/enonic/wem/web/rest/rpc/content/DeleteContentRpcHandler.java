package com.enonic.wem.web.rest.rpc.content;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.DeleteContents;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;


@Component
public final class DeleteContentRpcHandler
    extends AbstractDataRpcHandler
{
    public DeleteContentRpcHandler()
    {
        super( "content_delete" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final ContentPath contentPath = ContentPath.from( context.param( "contentPath" ).required().asString() );

        DeleteContents deleteContents = Commands.content().delete();
        deleteContents.deleter( AccountKey.anonymous() );
        deleteContents.paths( ContentPaths.from( contentPath ) );

        client.execute( deleteContents );
        // TODO: detect and report unsuccessful deletes
        context.setResult( DeleteContentJsonResult.successful() );
    }
}
