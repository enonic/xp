package com.enonic.wem.script;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptExports
{
    public ResourceKey getScript();

    public ScriptValue getValue();

    public boolean hasMethod( String name );

    public ScriptValue executeMethod( String name, Object... args );
}
