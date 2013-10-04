package com.enonic.wem.api.schema.content.form;


import com.enonic.wem.api.schema.mixin.QualifiedMixinName;

public class MixinNotFound
    extends RuntimeException
{
    private final QualifiedMixinName qualifiedMixinName;

    public MixinNotFound( final QualifiedMixinName qualifiedMixinName )
    {
        super( "Mixin not found: " + qualifiedMixinName );
        this.qualifiedMixinName = qualifiedMixinName;
    }
}
