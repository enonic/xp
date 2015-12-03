package com.enonic.xp.schema.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.content.validator.ContentTypeValidationResult;

@Beta
public interface ContentTypeService
{
    ContentType getByName( GetContentTypeParams params );

    ContentTypes getByApplication( ApplicationKey applicationKey );

    ContentTypes getAll( GetAllContentTypesParams params );

    ContentTypeValidationResult validate( ContentType type );
}
