package com.enonic.wem.core.schema.mixin;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinProvider;
import com.enonic.wem.api.schema.mixin.MixinRegistry;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.core.schema.BaseRegistry;

public final class MixinRegistryImpl
    extends BaseRegistry<MixinProvider, Mixin, Mixins, MixinName>
    implements MixinRegistry
{
    public MixinRegistryImpl()
    {
        super( MixinProvider.class, Mixin::getName );
    }

    public Mixin getMixin( final MixinName name )
    {
        return super.getItemByName( name );
    }

    public Mixins getMixinsByModule( final ModuleKey moduleKey )
    {
        final Mixins mixins = super.getItemsByModule( moduleKey );
        return mixins == null ? Mixins.empty() : mixins;
    }

    public Mixins getAllMixins()
    {
        return Mixins.from( super.getAllItems() );
    }
}
