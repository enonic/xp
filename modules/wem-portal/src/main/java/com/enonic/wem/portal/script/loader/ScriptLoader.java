package com.enonic.wem.portal.script.loader;

import com.enonic.wem.api.module.ModuleResourceKey;

public interface ScriptLoader
{
    public ScriptSource load( String name );

    public ScriptSource load( ModuleResourceKey key );
}
