package com.enonic.wem.core.script.resolver;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptResolver
{
    public ResourceKey getResource();

    public ResourceKey resolveScript( String name );

    public ResourceKey resolveResource( String name );
}
