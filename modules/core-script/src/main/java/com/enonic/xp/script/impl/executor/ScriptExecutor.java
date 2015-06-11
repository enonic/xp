package com.enonic.xp.script.impl.executor;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;

public interface ScriptExecutor
{
    ScriptExports execute( ResourceKey script );
}
