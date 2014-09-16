package com.enonic.wem.core.schema.mixin.dao;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.Mixins;

public interface MixinDao
{
    Mixins getAllMixins();

    Mixin getMixin( MixinName mixinName );
}
