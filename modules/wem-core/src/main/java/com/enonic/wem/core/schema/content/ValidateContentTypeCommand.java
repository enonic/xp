package com.enonic.wem.core.schema.content;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.ValidateContentTypeParams;
import com.enonic.wem.api.schema.content.validator.ContentTypeSuperTypeValidator;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;


final class ValidateContentTypeCommand
{
    private ContentTypeService contentTypeService;

    private ValidateContentTypeParams params;

    ContentTypeValidationResult execute()
    {
        params.validate();

        return doExecute();
    }

    private ContentTypeValidationResult doExecute()
    {
        ContentType contentType = params.getContentType();

        ContentTypeSuperTypeValidator validator = ContentTypeSuperTypeValidator.
            newContentTypeSuperTypeValidator().
            contentTypeService( this.contentTypeService ).
            build();

        validator.validate( contentType.getName(), contentType.getSuperType() );
        return validator.getResult();
    }

    ValidateContentTypeCommand params( final ValidateContentTypeParams params )
    {
        this.params = params;
        return this;
    }

    ValidateContentTypeCommand contentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
        return this;
    }
}
