package com.enonic.wem.api.command.schema.content;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;

public interface ContentTypeService
{
    ContentType getByName( GetContentTypeParams params );

    ContentTypes getByNames( GetContentTypesParams params );

    ContentTypes getAll( GetAllContentTypesParams params );

    ContentTypes getRoots();

    ContentTypes getChildren( GetChildContentTypesParams params );

    ContentType create( CreateContentTypeParams params );

    UpdateContentTypeResult update( UpdateContentTypeParams params );

    DeleteContentTypeResult delete( DeleteContentTypeParams params );

    ContentTypeValidationResult validate( ValidateContentTypeParams params );
}
