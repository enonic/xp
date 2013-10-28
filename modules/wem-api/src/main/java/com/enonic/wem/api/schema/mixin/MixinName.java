package com.enonic.wem.api.schema.mixin;


import com.enonic.wem.api.schema.SchemaName;

public class MixinName
    extends SchemaName
{
    private MixinName( final String name )
    {
        super( name );
    }

    public static MixinName from( final String mixinName )
    {
        return new MixinName( mixinName );
    }
}
