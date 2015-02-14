package com.enonic.xp.schema.content;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.content.validator.ContentTypeValidationResult;

public interface ContentTypeService
{
    ContentType getByName( GetContentTypeParams params );

    ContentTypes getByNames( GetContentTypesParams params );

    ContentTypes getByModule( ModuleKey moduleKey );

    ContentTypes getAll( GetAllContentTypesParams params );

    ContentTypes getChildren( GetChildContentTypesParams params );

    ContentTypeValidationResult validate( ContentType type );
}
