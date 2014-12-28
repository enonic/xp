package com.enonic.wem.admin.rest.resource.schema.mixin;


import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinService;

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
