package com.enonic.xp.script.impl;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.script.runtime.ScriptRuntime;

public interface ScriptRuntimeInternal
    extends ScriptRuntime
{
    void runDisposers( ApplicationKey key );
}
