package com.enonic.wem.api.schema.content;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.validator.ContentTypeValidationResult;

public interface ContentTypeService
{
    ContentType getByName( GetContentTypeParams params );

    ContentTypes getByNames( GetContentTypesParams params );

    ContentTypes getByModule( ModuleKey moduleKey );

    ContentTypes getAll( GetAllContentTypesParams params );

    ContentTypes getChildren( GetChildContentTypesParams params );

    ContentTypeValidationResult validate( ContentType type );
}
