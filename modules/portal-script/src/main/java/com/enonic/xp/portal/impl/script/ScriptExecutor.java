package com.enonic.xp.portal.impl.script;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.portal.script.ScriptValue;

public interface ScriptExecutor
{
    public Object executeMain();

    public Object executeRequire( ResourceKey script );

    public ScriptValue newScriptValue( Object value );
}
