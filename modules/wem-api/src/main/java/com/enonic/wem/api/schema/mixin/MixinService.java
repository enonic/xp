package com.enonic.wem.api.schema.mixin;

import com.enonic.wem.api.module.ModuleKey;

public interface MixinService
{
    Mixin getByName( GetMixinParams params );

    Mixins getByNames( GetMixinsParams params );

    Mixins getAll();

    Mixins getByModule( ModuleKey moduleKey );
}
