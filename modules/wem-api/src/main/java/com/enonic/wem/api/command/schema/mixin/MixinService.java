package com.enonic.wem.api.command.schema.mixin;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;

public interface MixinService
{
    Mixin create( CreateMixinParams params );

    UpdateMixinResult update( UpdateMixinParams params );

    DeleteMixinResult delete( DeleteMixinParams params );

    Mixin getByName( GetMixinParams params );

    Mixins getByNames( GetMixinsParams params );

    Mixins getAll();
}
