package com.enonic.wem.web.rest.rpc.content;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.DeleteContent;
import com.enonic.wem.api.command.content.DeleteContentResult;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.web.rest.rpc.content.DeleteContentJsonResult.newDeleteContentJsonResult;

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
