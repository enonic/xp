package com.enonic.xp.admin.impl.json.schema.mixin;

import java.util.List;

public class MixinListJson
{
    private final List<MixinJson> list;

    public MixinListJson( final List<MixinJson> list )
    {
        this.list = list;
    }

    public List<MixinJson> getMixins()
    {
        return this.list;
    }
}
