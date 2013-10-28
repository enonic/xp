package com.enonic.wem.api.command.schema.mixin;

import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinNames;

public final class MixinGetCommands
{
    public GetMixin byName( final MixinName name )
    {
        return new GetMixin().name( name );
    }

    public GetMixins byNames( final MixinNames names )
    {
        return new GetMixins().qualifiedNames( names );
    }

    public GetMixins all()
    {
        return new GetMixins().all();
    }
}
