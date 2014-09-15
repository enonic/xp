package com.enonic.wem.api.schema.content;

import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;

public interface ContentTypeService
{
    ContentType getByName( GetContentTypeParams params );

    ContentTypes getByNames( GetContentTypesParams params );

    ContentTypes getAll( GetAllContentTypesParams params );

    ContentTypes getRoots();

    ContentTypes getChildren( GetChildContentTypesParams params );

    ContentTypeValidationResult validate( ValidateContentTypeParams params );
}
