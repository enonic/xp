package com.enonic.xp.script.impl;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptSettings;

/**
 * This interface is here only to support multiple runtimes. It's not going to be here permanent.
 */
public interface ScriptRuntimeProvider
{
    ScriptRuntimeInternal create( ScriptSettings settings );

    void dispose( ScriptRuntime runtime );

    void invalidate( ApplicationKey key );

    void runDisposers( ApplicationKey key );
}
