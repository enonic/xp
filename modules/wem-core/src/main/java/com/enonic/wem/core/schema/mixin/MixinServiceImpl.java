package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.mixin.Mixins;

public class MixinServiceImpl
    implements MixinService
{
    private MixinRegistry registry;

    @Override
    public Mixin getByName( final MixinName name )
    {
        return this.registry.getMixin( name );
    }

    @Override
    public Mixins getAll()
    {
        return this.registry.getAllMixins();
    }

    @Override
    public Mixins getByModule( final ModuleKey moduleKey )
    {
        return this.registry.getMixinsByModule( moduleKey );
    }

    public void setRegistry( final MixinRegistry registry )
    {
        this.registry = registry;
    }
}
