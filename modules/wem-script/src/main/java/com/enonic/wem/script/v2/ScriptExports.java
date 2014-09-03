package com.enonic.wem.script.v2;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptExports
{
    public ResourceKey getScript();

    public boolean hasProperty( String name );

    public Object executeMethod( String name, Object... args );
}
