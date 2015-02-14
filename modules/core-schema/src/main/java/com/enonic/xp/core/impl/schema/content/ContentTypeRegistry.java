package com.enonic.xp.core.impl.schema.content;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeProvider;
import com.enonic.xp.schema.content.ContentTypes;

interface ContentTypeRegistry
{
    ContentType get( ContentTypeName name );

    ContentTypes getByModule( ModuleKey moduleKey );

    ContentTypes getAll();

    void addProvider( ContentTypeProvider provider );

    void removeProvider( ContentTypeProvider provider );
}
