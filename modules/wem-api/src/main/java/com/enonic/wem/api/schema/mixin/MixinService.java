package com.enonic.wem.api.schema.mixin;

public interface MixinService
{
    Mixin create( CreateMixinParams params );

    UpdateMixinResult update( UpdateMixinParams params );

    DeleteMixinResult delete( DeleteMixinParams params );

    Mixin getByName( GetMixinParams params );

    Mixins getByNames( GetMixinsParams params );

    Mixins getAll();
}
