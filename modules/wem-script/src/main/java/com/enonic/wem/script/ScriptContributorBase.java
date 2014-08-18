package com.enonic.wem.script;

import java.util.Map;

import com.google.common.collect.Maps;

public abstract class ScriptContributorBase
    implements ScriptContributor
{
    private final Map<String, String> libraries;

    private final Map<String, Object> globalVariables;

    public ScriptContributorBase()
    {
        this.libraries = Maps.newHashMap();
        this.globalVariables = Maps.newHashMap();
    }

    @Override
    public final Map<String, String> getLibraries()
    {
        return this.libraries;
    }

    @Override
    public final Map<String, Object> getGlobalVariables()
    {
        return this.globalVariables;
    }

    protected final void addLibrary( final String name, final String path )
    {
        this.libraries.put( name, path );
    }

    protected final void addGlobalVariable( final String name, final Object variable )
    {
        this.globalVariables.put( name, variable );
    }
}
