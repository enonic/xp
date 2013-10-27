package com.enonic.wem.api.command.schema.mixin;

import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;

public final class MixinGetCommands
{
    public GetMixin byQualifiedName( final QualifiedMixinName qualifiedName )
    {
        return new GetMixin().qualifiedName( qualifiedName );
    }

    public GetMixins byQualifiedNames( final QualifiedMixinNames qualifiedNames )
    {
        return new GetMixins().names( qualifiedNames );
    }

    public GetMixins all()
    {
        return new GetMixins().all();
    }
}
