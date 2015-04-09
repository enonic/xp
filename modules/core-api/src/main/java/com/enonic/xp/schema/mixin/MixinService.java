package com.enonic.xp.schema.mixin;

import com.google.common.annotations.Beta;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.content.ContentType;

@Beta
public interface MixinService
{
    Mixin getByName( MixinName name );

    Mixin getByLocalName( String localName );

    Mixins getAll();

    Mixins getByModule( ModuleKey moduleKey );

    Mixins getByContentType( ContentType contentType );
}
