package com.enonic.xp.launcher.impl.weaver;

import com.enonic.xp.trace.Traced;

/**
 * Interface fixture: the woven default method body becomes a private interface method.
 */
public interface TracedInterfaceFixture
{
    @Traced("fixture.iface")
    default String greet( final String who )
    {
        return "Hi " + who;
    }
}
