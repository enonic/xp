package com.enonic.wem.api.schema.mixin;

import java.text.MessageFormat;

import com.google.common.base.Joiner;

import com.enonic.wem.api.exception.NotFoundException;

public final class MixinNotFoundException
    extends NotFoundException
{
    public MixinNotFoundException( final MixinName mixinName )
    {
        super( "Mixin [{0}] was not found", mixinName );
    }

    public MixinNotFoundException( final MixinNames mixinNames )
    {
        super( MessageFormat.format( "Mixins with names [{0}] were not found", Joiner.on( ", " ).join( mixinNames ) ) );
    }
}
