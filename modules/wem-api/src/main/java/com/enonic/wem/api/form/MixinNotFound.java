package com.enonic.wem.api.form;


import com.enonic.wem.api.schema.mixin.MixinName;

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
