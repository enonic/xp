package com.enonic.wem.portal.script.loader;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptLoader
{
    public ScriptSource load( String name );

    public ScriptSource load( ResourceKey key );
}
