package com.enonic.wem.web.rest.rpc.content.schema.content;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.validator.ContentTypeValidationError;
import com.enonic.wem.api.content.schema.content.validator.ContentTypeValidationResult;
import com.enonic.wem.core.content.schema.content.serializer.ContentTypeJsonSerializer;
import com.enonic.wem.web.json.JsonResult;

final class ValidateContentTypeJsonResult
    extends JsonResult
{
    private final static ContentTypeJsonSerializer contentTypeSerializer = new ContentTypeJsonSerializer().includeQualifiedName( true );

    private final ContentTypeValidationResult contentTypeValidationResult;

    private final ContentType contentType;

    ValidateContentTypeJsonResult( final ContentTypeValidationResult contentTypeValidationResult, final ContentType contentType )
    {
        this.contentTypeValidationResult = contentTypeValidationResult;
        this.contentType = contentType;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "hasErrors", contentTypeValidationResult.hasErrors() );
        final ArrayNode errorItems = json.putArray( "errors" );
        for ( ContentTypeValidationError validationError : contentTypeValidationResult )
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

    private JsonNode serialize( final ContentTypeValidationError validationError )
    {
        final ObjectNode validationNode = objectNode();
        validationNode.put( "message", validationError.getErrorMessage() );
        return validationNode;
    }

}
