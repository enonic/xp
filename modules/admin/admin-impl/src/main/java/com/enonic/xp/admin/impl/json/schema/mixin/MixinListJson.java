package com.enonic.xp.admin.impl.json.schema.mixin;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.MixinIconUrlResolver;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.Mixins;

public class MixinListJson
{
    private final ImmutableList<MixinJson> list;

    public MixinListJson( final ImmutableList<MixinJson> list )
    {
        this.list = list;
    }

    public MixinListJson( final Mixins mixins, final MixinIconUrlResolver iconUrlResolver,
                          final LocaleMessageResolver localeMessageResolver )
    {
        final ImmutableList.Builder<MixinJson> builder = ImmutableList.builder();
        for ( final Mixin mixin : mixins )
        {
            builder.add( new MixinJson( mixin, iconUrlResolver, localeMessageResolver ) );
        }

        this.list = builder.build();
    }

    public List<MixinJson> getMixins()
    {
        return this.list;
    }
}
