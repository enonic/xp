package com.enonic.wem.web.rest.rpc.content.type;


import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.type.DeleteContentTypes;
import com.enonic.wem.api.content.type.ContentTypeDeletionResult;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public final class DeleteContentTypeRpcHandler
    extends AbstractDataRpcHandler
{
    public DeleteContentTypeRpcHandler()
    {
        super( "contentType_delete" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final QualifiedContentTypeNames contentTypeNames =
            QualifiedContentTypeNames.from( context.param( "qualifiedContentTypeNames" ).required().asStringArray() );

        final DeleteContentTypes deleteContentTypes = Commands.contentType().delete();
        deleteContentTypes.names( contentTypeNames );
        ContentTypeDeletionResult deletionResult = client.execute( deleteContentTypes );
        context.setResult( new DeleteContentTypeJsonResult( deletionResult ) );
    }
}
