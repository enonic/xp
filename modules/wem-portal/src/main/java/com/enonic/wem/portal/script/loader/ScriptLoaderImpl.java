package com.enonic.wem.portal.script.loader;

import com.enonic.wem.api.module.ModuleResourceKey;

public final class ScriptLoaderImpl
    implements ScriptLoader
{
    @Override
    public ScriptSource load( final String name )
    {
        return load( ModuleResourceKey.from( name ) );
    }

    @Override
    public ScriptSource load( final ModuleResourceKey key )
    {
        return new ScriptSourceImpl( key );
    }
}
