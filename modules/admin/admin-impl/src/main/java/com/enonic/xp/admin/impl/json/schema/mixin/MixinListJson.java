package com.enonic.xp.admin.impl.json.schema.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.MixinIconUrlResolver;
import com.enonic.xp.schema.mixin.Mixins;

public class MixinListJson
{
    private final List<MixinJson> list;

    public MixinListJson()
    {
        this.list = new ArrayList<MixinJson>();
    }


    public MixinListJson( final List<MixinJson> list )
    {
        this.list = new ArrayList<MixinJson>( list );
    }

    public MixinListJson( final Mixins mixins, final MixinIconUrlResolver iconUrlResolver,
                          final LocaleMessageResolver localeMessageResolver )
    {
        this.list = mixins.stream().map(
            mixin -> MixinJson.create().setMixin( mixin ).setIconUrlResolver( iconUrlResolver ).setLocaleMessageResolver(
                localeMessageResolver ).build() ).collect( Collectors.toList() );
    }

    public void addMixins( final List<MixinJson> mixins )
    {
        this.list.addAll( mixins );
    }

    public List<MixinJson> getMixins()
    {
        return this.list;
    }
}
