package com.enonic.wem.web.rest.rpc.content.type;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.InvalidContentTypeException;
import com.enonic.wem.api.content.type.ValidateContentTypeResult;
import com.enonic.wem.core.content.XmlParsingException;
import com.enonic.wem.core.content.type.ContentTypeXmlSerializer;
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
            final InvalidContentTypeException invalidContentType = new InvalidContentTypeException( null, "Invalid content type format" );
            context.setResult( new ValidateContentTypeJsonResult( ValidateContentTypeResult.from( invalidContentType ) ) );
            return;
        }

        final ValidateContentTypeResult validationResult = client.execute( contentType().validate().contentType( contentType ) );
        context.setResult( new ValidateContentTypeJsonResult( validationResult, contentType ) );
    }

}
