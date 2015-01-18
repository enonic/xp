package com.enonic.wem.admin.json.schema.mixin;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.rest.resource.schema.mixin.MixinIconUrlResolver;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;

public class MixinListJson
{
    private final MixinIconUrlResolver iconUrlResolver;

    private final Mixins mixins;

    public MixinListJson( final Mixins mixins, final MixinIconUrlResolver iconUrlResolver )
    {
        this.mixins = mixins;
        this.iconUrlResolver = iconUrlResolver;
    }

    public List<MixinJson> getMixins()
    {
        final ImmutableList.Builder<MixinJson> builder = ImmutableList.builder();
        for ( Mixin mixin : mixins )
        {
            builder.add( new MixinJson( mixin, iconUrlResolver ) );
        }
        return builder.build();
    }
}
