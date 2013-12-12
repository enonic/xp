package com.enonic.wem.portal.script.loader;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.core.module.ModuleKeyResolver;

public interface ScriptLoader
{
    public ScriptSource load( String name );

    public ScriptSource load( ModuleKeyResolver resolver, ModuleKey defaultModule, String name );

    public ScriptSource loadFromSystem( String name );

    public ScriptSource loadFromModule( ModuleResourceKey key );
}
