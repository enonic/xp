package com.enonic.wem.core.schema.mixin.dao;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.Mixins;

public interface MixinDao
{
    Mixin createMixin( Mixin mixin );

    void updateMixin( Mixin mixin );

    Mixins getAllMixins();

    Mixin.Builder getMixin( MixinName mixinName );

    boolean deleteMixin( MixinName mixinName );
}
