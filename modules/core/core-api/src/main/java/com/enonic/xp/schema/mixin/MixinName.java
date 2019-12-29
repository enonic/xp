package com.enonic.xp.schema.mixin;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.schema.BaseSchemaName;

@PublicApi
public final class MixinName
    extends BaseSchemaName
    implements Comparable<MixinName>
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

    public static MixinName from( final String mixinName )
    {
        return new MixinName( mixinName );
    }

    public String getApplicationPrefix() {
        return this.getApplicationKey() == null ? "" : this.getApplicationKey().toString().replace( '.', '-' );
    }

    @Override
    public int compareTo( final MixinName that )
    {
        return this.toString().compareTo( that.toString() );
    }
}
