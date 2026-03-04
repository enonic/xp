package com.enonic.xp.schema.content;

import java.util.Set;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.content.validator.ContentTypeValidationResult;


public interface ContentTypeService
{
    ContentType getByName( GetContentTypeParams params );

    ContentTypes getByApplication( ApplicationKey applicationKey );

    ContentTypes getAll();

    Set<String> getMimeTypes( ContentTypeNames napes );

    ContentTypeValidationResult validate( ContentType type );
}
