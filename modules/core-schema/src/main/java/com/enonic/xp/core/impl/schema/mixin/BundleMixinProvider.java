package com.enonic.xp.core.impl.schema.mixin;

import org.osgi.framework.Bundle;

import com.enonic.wem.api.schema.mixin.MixinProvider;
import com.enonic.wem.api.schema.mixin.Mixins;

public final class BundleMixinProvider
    implements MixinProvider
{
    private final Mixins mixins;

    private BundleMixinProvider( final Mixins mixins )
    {
        this.mixins = mixins;
    }

    @Override
    public Mixins get()
    {
        return this.mixins;
    }

    public static BundleMixinProvider create( final Bundle bundle )
    {
        final Mixins mixins = new MixinLoader( bundle ).loadMixins();
        return mixins != null ? new BundleMixinProvider( mixins ) : null;
    }
}
