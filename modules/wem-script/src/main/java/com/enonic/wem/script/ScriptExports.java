package com.enonic.wem.script;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptExports
{
    public ResourceKey getScript();

    public boolean hasMethod( String name );

    public ScriptObject executeMethod( String name, Object... args );
}
