package com.enonic.wem.admin.rest.resource.schema.content.model;

import com.enonic.wem.api.schema.content.validator.ContentTypeValidationError;

public class ContentTypeValidationErrorJson
{
    private final String message;

    public ContentTypeValidationErrorJson( final ContentTypeValidationError validationError )
    {
        message = validationError.getErrorMessage();
    }

    public String getMessage()
    {
        return message;
    }
}
