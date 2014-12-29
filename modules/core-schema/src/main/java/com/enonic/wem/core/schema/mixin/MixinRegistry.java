package com.enonic.wem.core.schema.mixin;

import java.util.Collection;

import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.Mixins;

public interface MixinRegistry
{
    public Mixin get( MixinName name );

    public Collection<Mixin> getAll();

    public void addMixins( Mixins mixins );

    public void removeMixins( Mixins mixins );
}
