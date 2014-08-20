package com.enonic.wem.thymeleaf;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.ScriptContributor;
import com.enonic.wem.script.internal.ScriptEnvironment;

public final class SimpleScriptEnvironment
    implements ScriptEnvironment
{
    private final Map<String, ResourceKey> libraries;

    private final Map<String, Object> variables;

    public SimpleScriptEnvironment()
    {
        this.libraries = Maps.newHashMap();
        this.variables = Maps.newHashMap();
    }

    @Override
    public ResourceKey getLibrary( final String name )
    {
        return this.libraries.get( name );
    }

    @Override
    public Object getVariable( final String name )
    {
        return this.variables.get( name );
    }

    public void addContributor( final String moduleKey, final ScriptContributor contributor )
    {
        addContributor( ModuleKey.from( moduleKey ), contributor );
    }

    public void addContributor( final ModuleKey moduleKey, final ScriptContributor contributor )
    {
        this.variables.putAll( contributor.getVariables() );

        for ( final Map.Entry<String, String> library : contributor.getLibraries().entrySet() )
        {
            this.libraries.put( library.getKey(), ResourceKey.from( moduleKey, library.getValue() ) );
        }
    }
}
