package com.enonic.wem.admin.rest.rpc.schema.content;


import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.DeleteContentType;
import com.enonic.wem.api.command.schema.content.DeleteContentTypeResult;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;


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

        final ContentTypeDeletionResult deletionResult = new ContentTypeDeletionResult();
        for ( QualifiedContentTypeName contentTypeName : contentTypeNames )
        {
            final DeleteContentType deleteContentType = Commands.contentType().delete().name( contentTypeName );
            final DeleteContentTypeResult deleteResult = client.execute( deleteContentType );
            switch ( deleteResult )
            {
                case SUCCESS:
                    deletionResult.success( contentTypeName );
                    break;

                case NOT_FOUND:
                    deletionResult.failure( contentTypeName, String.format( "ContentType [%s] was not found", contentTypeName ) );
                    break;

                case UNABLE_TO_DELETE:
                    deletionResult.failure( contentTypeName,
                                            String.format( "Unable to delete content type [%s]: Content type is being used",
                                                           contentTypeName ) );
                    break;
            }
        }

        context.setResult( new DeleteContentTypeJsonResult( deletionResult ) );
    }
}
