package com.enonic.xp.core.schema.mixin;

import com.enonic.xp.core.module.ModuleKey;
import com.enonic.xp.core.schema.content.ContentType;

public interface MixinService
{
    Mixin getByName( MixinName name );

    Mixin getByLocalName( String localName );

    Mixins getAll();

    Mixins getByModule( ModuleKey moduleKey );

    Mixins getByContentType( ContentType contentType );
}
