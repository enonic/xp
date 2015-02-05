package com.enonic.wem.api.schema.mixin;

import com.enonic.wem.api.module.ModuleKey;

public interface MixinService
{
    Mixin getByName( MixinName name );

    Mixin getByLocalName( String localName );

    Mixins getAll();

    Mixins getByModule( ModuleKey moduleKey );
}
