package com.enonic.wem.script;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptExports
{
    public ResourceKey getScript();

    public ScriptObject getValue();

    public boolean hasMethod( String name );

    public ScriptObject executeMethod( String name, Object... args );
}
