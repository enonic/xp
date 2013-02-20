package com.enonic.wem.web.rest.rpc.content;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.type.validator.DataValidationError;
import com.enonic.wem.api.content.schema.type.validator.DataValidationErrors;
import com.enonic.wem.web.json.JsonResult;

final class ValidateContentDataJsonResult
    extends JsonResult
{
    private final DataValidationErrors dataValidationErrors;

    ValidateContentDataJsonResult( final DataValidationErrors dataValidationErrors )
    {
        this.dataValidationErrors = dataValidationErrors;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "success", true );
        json.put( "hasErrors", dataValidationErrors.hasErrors() );
        final ArrayNode errorItems = json.putArray( "errors" );
        for ( DataValidationError validationError : dataValidationErrors )
        {
            serialize( validationError, errorItems.addObject() );
        }
    }

    private void serialize( final DataValidationError validationError, final ObjectNode json )
    {
        json.put( "path", validationError.getPath().toString() );
        json.put( "message", validationError.getErrorMessage() );
    }
}
