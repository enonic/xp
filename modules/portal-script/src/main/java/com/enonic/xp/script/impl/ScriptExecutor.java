package com.enonic.xp.script.impl;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.xp.portal.script.ScriptValue;

public interface ScriptExecutor
{
    public Object executeMain();

    public Object executeRequire( ResourceKey script );

    public ScriptValue newScriptValue( Object value );
}
