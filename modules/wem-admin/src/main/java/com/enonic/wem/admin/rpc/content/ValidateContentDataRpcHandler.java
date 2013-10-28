package com.enonic.wem.admin.rpc.content;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.validator.DataValidationErrors;

import static com.enonic.wem.api.command.Commands.content;
import static com.enonic.wem.api.command.Commands.contentType;


public final class ValidateContentDataRpcHandler
    extends AbstractDataRpcHandler
{

    public ValidateContentDataRpcHandler()
    {
        super( "content_validate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String contentTypeParam = context.param( "contentTypeName" ).required().asString();
        final ObjectNode contentDataParam = context.param( "contentData" ).required().asObject();

        final ContentTypeName contentTypeName =  ContentTypeName.from( contentTypeParam );

        final ContentType contentType = getContentType( contentTypeName );
        final ContentData contentData = new ContentDataParser( contentType ).parse( contentDataParam );

        final DataValidationErrors validationErrors =
            client.execute( content().validate().contentData( contentData ).contentType( contentTypeName ) );

        final ValidateContentDataJsonResult result = new ValidateContentDataJsonResult( validationErrors );
        context.setResult( result );
    }

    private ContentType getContentType( final ContentTypeName contentTypeName )
    {
        final GetContentTypes getContentType =
            contentType().get().qualifiedNames( ContentTypeNames.from( contentTypeName ) );
        final ContentTypes contentTypes = client.execute( getContentType );
        return contentTypes.first();
    }
}
