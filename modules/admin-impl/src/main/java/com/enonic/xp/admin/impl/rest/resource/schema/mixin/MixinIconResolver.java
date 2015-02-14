package com.enonic.xp.admin.impl.rest.resource.schema.mixin;


import com.enonic.xp.core.icon.Icon;
import com.enonic.xp.core.schema.mixin.Mixin;
import com.enonic.xp.core.schema.mixin.MixinName;
import com.enonic.xp.core.schema.mixin.MixinService;

public final class MixinIconResolver
{
    private final MixinService mixinService;

    public MixinIconResolver( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }

    public Icon resolveIcon( final MixinName name )
    {
        final Mixin mixin = mixinService.getByName( name );
        return mixin == null ? null : mixin.getIcon();
    }

}
