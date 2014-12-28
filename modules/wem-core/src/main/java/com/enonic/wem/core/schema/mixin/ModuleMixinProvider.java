package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.schema.mixin.Mixins;

public final class ModuleMixinProvider
    implements MixinProvider
{
    private final Module module;

    public ModuleMixinProvider( final Module module )
    {
        this.module = module;
    }

    @Override
    public Mixins get()
    {
        return new MixinLoader().loadMixins( module );
    }
}
