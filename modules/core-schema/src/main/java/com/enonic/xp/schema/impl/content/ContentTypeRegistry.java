package com.enonic.xp.schema.impl.content;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeProvider;
import com.enonic.wem.api.schema.content.ContentTypes;

interface ContentTypeRegistry
{
    ContentType get( ContentTypeName name );

    ContentTypes getByModule( ModuleKey moduleKey );

    ContentTypes getAll();

    void addProvider( ContentTypeProvider provider );

    void removeProvider( ContentTypeProvider provider );
}
