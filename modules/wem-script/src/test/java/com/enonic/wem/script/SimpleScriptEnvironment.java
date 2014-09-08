package com.enonic.wem.script;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.script.internal.ScriptEnvironment;

public final class SimpleScriptEnvironment
    implements ScriptEnvironment
{
    private final Map<String, ScriptLibrary> libraries;

    public SimpleScriptEnvironment()
    {
        this.libraries = Maps.newHashMap();
    }

    @Override
    public ScriptLibrary getLibrary( final String name )
    {
        return this.libraries.get( name );
    }

    public void addLibrary( final ScriptLibrary library )
    {
        this.libraries.put( library.getName(), library );
    }
}
