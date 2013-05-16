package com.enonic.wem.web.rest.rpc.content.schema.content;


import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.core.content.schema.content.serializer.ContentTypeXmlSerializer;
import com.enonic.wem.core.support.serializer.XmlParsingException;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.command.Commands.contentType;


public class ValidateContentTypeRpcHandler
    extends AbstractDataRpcHandler
{
    private final ContentTypeXmlSerializer contentTypeXmlSerializer = new ContentTypeXmlSerializer();

    public ValidateContentTypeRpcHandler()
    {
        super( "contentType_validate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String contentTypeXml = context.param( "contentType" ).required().asString();
        final ContentType contentType;
        try
        {
            contentType = contentTypeXmlSerializer.toContentType( contentTypeXml );
        }
        catch ( XmlParsingException e )
        {
            context.setResult( new JsonErrorResult( e.getMessage() ) );
            return;
        }

        final ContentTypeValidationResult validationResult = client.execute( contentType().validate().contentType( contentType ) );
        context.setResult( new ValidateContentTypeJsonResult( validationResult, contentType ) );
    }

}
