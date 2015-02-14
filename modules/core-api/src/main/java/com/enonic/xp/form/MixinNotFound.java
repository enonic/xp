package com.enonic.xp.form;


import com.enonic.xp.schema.mixin.MixinName;

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
