package com.enonic.wem.api.schema.mixin;

public interface MixinService
{
    Mixin getByName( GetMixinParams params );

    Mixins getByNames( GetMixinsParams params );

    Mixins getAll();
}
