package com.enonic.wem.portal.script.loader;

import com.google.common.base.Optional;

import com.enonic.wem.api.module.ModuleResourceKey;

public interface ScriptLoader
{
    public Optional<ScriptSource> loadFromSystem( String name );

    public Optional<ScriptSource> loadFromModule( ModuleResourceKey key );
}
