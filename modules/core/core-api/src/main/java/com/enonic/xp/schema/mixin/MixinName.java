package com.enonic.xp.schema.mixin;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.BaseSchemaName;

@PublicApi
public final class MixinName
    extends BaseSchemaName
{
    private MixinName( final String name )
    {
        super( name );
    }

    private MixinName( final ApplicationKey applicationKey, final String localName )
    {
        super( applicationKey, localName );
    }

    public static MixinName from( final ApplicationKey applicationKey, final String localName )
    {
        return new MixinName( applicationKey, localName );
    }

    public static MixinName from( final String value )
    {
        return new MixinName( value );
    }
}
