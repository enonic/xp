package com.enonic.xp.testing.script;

import com.enonic.xp.resource.ResourceKey;

public abstract class ScriptBeanTestSupport
    extends ScriptTestSupport
{
    protected final MockBeanContext newBeanContext( final ResourceKey key )
    {
        return new MockBeanContext( key, this.bundleContext );
    }
}
