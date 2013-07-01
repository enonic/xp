package com.enonic.wem.admin.rest.rpc.content;


import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.DeleteContent;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.DeleteContentResult;

import static com.enonic.wem.admin.rest.rpc.content.DeleteContentJsonResult.newDeleteContentJsonResult;


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
        final ContentPaths contentsToDelete = ContentPaths.from( context.param( "contentPaths" ).required().asStringArray() );

        final DeleteContentJsonResult.Builder jsonResult = newDeleteContentJsonResult();

        for ( final ContentPath contentToDelete : contentsToDelete )
        {
            final DeleteContent deleteContent = Commands.content().delete();
            deleteContent.deleter( AccountKey.anonymous() );
            deleteContent.selector( contentToDelete );
            final DeleteContentResult commandResult = client.execute( deleteContent );
            jsonResult.registerResult( contentToDelete, commandResult );
        }

        context.setResult( jsonResult.build() );
    }
}
