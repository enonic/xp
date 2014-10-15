package com.enonic.wem.core.schema.mixin.dao;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinRegistry;
import com.enonic.wem.api.schema.mixin.Mixins;

public final class MixinDaoImpl
    implements MixinDao
{
    private MixinRegistry mixinRegistry;

    @Override
    public Mixins getAllMixins()
    {
        return this.mixinRegistry.getAllMixins();
    }

    @Override
    public Mixin getMixin( final MixinName mixinName )
    {
        return this.mixinRegistry.getMixin( mixinName );
    }

    public void setMixinRegistry( final MixinRegistry mixinRegistry )
    {
        this.mixinRegistry = mixinRegistry;
    }
}
