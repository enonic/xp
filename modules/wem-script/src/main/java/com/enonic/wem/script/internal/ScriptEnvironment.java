package com.enonic.wem.script.internal;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptEnvironment
{
    public ResourceKey getLibrary( String name );

    public Object getVariable( String name );
}
