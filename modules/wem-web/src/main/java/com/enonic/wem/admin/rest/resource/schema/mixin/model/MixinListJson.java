package com.enonic.wem.admin.rest.resource.schema.mixin.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;

public class MixinListJson
{
    private final Mixins mixins;

    public MixinListJson(final Mixins mixins)
    {
        this.mixins = mixins;
    }

    public List<MixinJson> getMixins()
    {
        final ImmutableList.Builder<MixinJson> builder = ImmutableList.builder();
        for ( Mixin mixin : mixins )
        {
            builder.add( new MixinJson( mixin ) );
        }
        return builder.build();
    }
}
