package com.enonic.xp.schema.mixin;

import com.google.common.annotations.Beta;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.BaseSchemaName;

@Beta
public final class MixinName
    extends BaseSchemaName
    implements Comparable<MixinName>
{
    private MixinName( final String name )
    {
        super( name );
    }

    private MixinName( final ModuleKey moduleKey, final String localName )
    {
        super( moduleKey, localName );
    }

    public static MixinName from( final ModuleKey moduleKey, final String localName )
    {
        return new MixinName( moduleKey, localName );
    }

    public static MixinName from( final String mixinName )
    {
        return new MixinName( mixinName );
    }

    @Override
    public int compareTo( final MixinName that )
    {
        return this.toString().compareTo( that.toString() );
    }
}
