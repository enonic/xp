package com.enonic.wem.api.schema.mixin;


import com.enonic.wem.api.content.QualifiedName;

public class QualifiedMixinName
    extends QualifiedName
{
    private QualifiedMixinName( final String name )
    {
        super( name );
    }

    public String getMixinName()
    {
        return getName();
    }

    public static QualifiedMixinName from( final String mixinName )
    {
        return new QualifiedMixinName( mixinName );
    }
}
