package com.enonic.wem.web.rest.rpc.content.type;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.InvalidContentTypeException;
import com.enonic.wem.api.content.type.ValidateContentTypeResult;
import com.enonic.wem.core.content.type.ContentTypeJsonSerializer;
import com.enonic.wem.web.json.JsonResult;

final class ValidateContentTypeJsonResult
    extends JsonResult
{
    private final static ContentTypeJsonSerializer contentTypeSerializer = new ContentTypeJsonSerializer();

    private final ValidateContentTypeResult validateContentTypeResult;

    private final ContentType contentType;

    ValidateContentTypeJsonResult( final ValidateContentTypeResult validateContentTypeResult, final ContentType contentType )
    {
        this.validateContentTypeResult = validateContentTypeResult;
        this.contentType = contentType;
    }

    ValidateContentTypeJsonResult( final ValidateContentTypeResult validateContentTypeResult )
    {
        this.validateContentTypeResult = validateContentTypeResult;
        this.contentType = null;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", true );
        json.put( "hasErrors", validateContentTypeResult.hasErrors() );
        final ArrayNode errorItems = json.putArray( "errors" );
        for ( InvalidContentTypeException validationError : validateContentTypeResult )
        {
            errorItems.add( serialize( validationError ) );
        }

        if ( contentType == null )
        {
            json.putNull( "contentType" );
        }
        else
        {
            json.put( "contentType", contentTypeSerializer.toJson( contentType ) );
        }
    }

    private JsonNode serialize( final InvalidContentTypeException validationError )
    {
        final ObjectNode validationNode = objectNode();
        validationNode.put( "message", validationError.getValidationMessage() );
        return validationNode;
    }

}
