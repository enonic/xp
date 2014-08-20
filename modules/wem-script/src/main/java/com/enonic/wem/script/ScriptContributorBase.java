package com.enonic.wem.script;

import java.util.Map;

import com.google.common.collect.Maps;

public abstract class ScriptContributorBase
    implements ScriptContributor
{
    private final Map<String, String> libraries;

    private final Map<String, Object> variables;

    public ScriptContributorBase()
    {
        this.libraries = Maps.newHashMap();
        this.variables = Maps.newHashMap();
    }

    @Override
    public final Map<String, String> getLibraries()
    {
        return this.libraries;
    }

    @Override
    public final Map<String, Object> getVariables()
    {
        return this.variables;
    }

    protected final void addLibrary( final String name, final String path )
    {
        this.libraries.put( name, path );
    }

    protected final void addVariable( final String name, final Object variable )
    {
        this.variables.put( name, variable );
    }
}
