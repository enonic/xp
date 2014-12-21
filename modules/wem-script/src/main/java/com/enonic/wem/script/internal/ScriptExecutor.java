package com.enonic.wem.script.internal;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.ScriptObject;

// TODO: Move into executor package
public interface ScriptExecutor
{
    public Object executeMain();

    public Object executeRequire( ResourceKey script );

    public ScriptObject newScriptValue( Object value );
}
