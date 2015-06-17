package com.enonic.xp.schema.content;

import com.google.common.annotations.Beta;

import com.enonic.xp.module.ModuleKey;

@Beta
public interface ContentTypeRegistry
{
    ContentType get( ContentTypeName name );

    ContentTypes getByModule( ModuleKey moduleKey );

    ContentTypes getAll();
}
