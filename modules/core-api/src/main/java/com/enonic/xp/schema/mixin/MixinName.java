package com.enonic.xp.schema.mixin;


import org.apache.commons.lang.StringUtils;

import com.enonic.xp.module.ModuleBasedName;
import com.enonic.xp.module.ModuleKey;

public final class MixinName
    extends ModuleBasedName
    implements Comparable<MixinName>
{
    public final static String SEPARATOR = ModuleBasedName.SEPARATOR;

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
        final String moduleKey = StringUtils.substringBefore( mixinName, SEPARATOR );
        final String localName = StringUtils.substringAfter( mixinName, SEPARATOR );
        return new MixinName( ModuleKey.from( moduleKey ), localName );
    }

    @Override
    public int compareTo( final MixinName that )
    {
        return this.toString().compareTo( that.toString() );
    }

}
