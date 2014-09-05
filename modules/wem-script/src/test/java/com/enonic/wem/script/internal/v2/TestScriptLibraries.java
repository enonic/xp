package com.enonic.wem.script.internal.v2;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.script.ScriptLibrary;

public final class TestScriptLibraries
    implements ScriptLibraries
{
    private final Map<String, ScriptLibrary> map;

    public TestScriptLibraries()
    {
        this.map = Maps.newHashMap();
        addLibrary( new AssertScriptLibrary() );
    }

    @Override
    public ScriptLibrary getLibrary( final String name )
    {
        return this.map.get( name );
    }

    public TestScriptLibraries addLibrary( final ScriptLibrary library )
    {
        this.map.put( library.getName(), library );
        return this;
    }
}
