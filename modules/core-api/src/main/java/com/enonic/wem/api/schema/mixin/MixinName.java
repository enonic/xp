package com.enonic.wem.api.schema.mixin;


import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.module.ModuleBasedName;
import com.enonic.wem.api.module.ModuleKey;

public final class MixinName
    extends ModuleBasedName
    implements Comparable<MixinName>
{
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

    public static String getSeparator() {
        return SEPARATOR;
    }

    @Override
    public int compareTo( final MixinName that )
    {
        return this.toString().compareTo( that.toString() );
    }

}
