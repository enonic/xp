package com.enonic.wem.core.schema.mixin;

import java.util.Collection;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;

public interface MixinRegistry
{
    public Mixin get( MixinName name );

    public Collection<Mixin> getAll();
}
