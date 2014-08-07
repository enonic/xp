package com.enonic.wem.portal.script.loader;

import com.enonic.wem.api.resource.ResourceKey;

public final class ScriptLoaderImpl
    implements ScriptLoader
{
    @Override
    public ScriptSource load( final String name )
    {
        return load( ResourceKey.from( name ) );
    }

    @Override
    public ScriptSource load( final ResourceKey key )
    {
        return new ScriptSourceImpl( key );
    }
}
