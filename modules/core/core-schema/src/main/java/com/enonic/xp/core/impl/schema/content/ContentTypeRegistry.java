package com.enonic.xp.core.impl.schema.content;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypes;

interface ContentTypeRegistry
{
    ContentType get( ContentTypeName name );

    ContentTypes getByApplication( ApplicationKey applicationKey );

    ContentTypes getAll();
}
