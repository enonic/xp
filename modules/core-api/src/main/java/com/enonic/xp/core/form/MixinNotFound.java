package com.enonic.xp.core.form;


import com.enonic.xp.core.schema.mixin.MixinName;

public class MixinNotFound
    extends RuntimeException
{
    private final MixinName mixinName;

    public MixinNotFound( final MixinName mixinName )
    {
        super( "Mixin not found: " + mixinName );
        this.mixinName = mixinName;
    }
}
