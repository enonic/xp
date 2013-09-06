package com.enonic.wem.admin.rest.resource.schema.content.json;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.json.schema.content.ContentTypeJson;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationError;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;

public class ValidateContentTypeJson
{
    private final boolean hasErrors;

    private final ContentTypeJson contentType;

    private final ImmutableList<ContentTypeValidationErrorJson> errorItems;

    public ValidateContentTypeJson( final ContentTypeValidationResult contentTypeValidationResult, final ContentType contentType )
    {
        this.contentType = ( contentType != null ) ? new ContentTypeJson( contentType ) : null;
        this.hasErrors = contentTypeValidationResult.hasErrors();

        final ImmutableList.Builder<ContentTypeValidationErrorJson> builder = ImmutableList.builder();
        for ( final ContentTypeValidationError validationError : contentTypeValidationResult )
        {
            builder.add( new ContentTypeValidationErrorJson( validationError ) );
        }

        this.errorItems = builder.build();
    }

    public boolean isHasErrors()
    {
        return hasErrors;
    }

    public ContentTypeJson getContentType()
    {
        return contentType;
    }

    public List<ContentTypeValidationErrorJson> getErrors()
    {
        return errorItems;
    }
}
