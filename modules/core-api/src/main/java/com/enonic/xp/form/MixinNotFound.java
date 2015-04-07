package com.enonic.xp.form;


import com.google.common.annotations.Beta;

import com.enonic.xp.schema.mixin.MixinName;

@Beta
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
