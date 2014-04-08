package com.enonic.wem.core.script.service;

import java.util.Map;

import javax.script.Bindings;

import com.google.common.collect.Maps;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.core.module.source.ModuleSource;
import com.enonic.wem.core.module.source.SourceResolver;
import com.enonic.wem.core.script.ScriptRunner;
import com.enonic.wem.core.script.engine.ExecutableScript;
import com.enonic.wem.core.script.engine.ScriptEngineService;

final class ScriptRunnerImpl
    implements ScriptRunner
{
    private final Map<String, Object> exports;

    protected SourceResolver sourceResolver;

    protected ScriptEngineService scriptEngineService;

    protected ModuleResourceKey resourceKey;

    protected Bindings bindings;

    public ScriptRunnerImpl()
    {
        this.exports = Maps.newHashMap();
    }

    @Override
    public ScriptRunner variable( final String name, final Object value )
    {
        this.bindings.put( name, value );
        return this;
    }

    @Override
    public void execute()
    {
        executeGlobal();

        final ModuleResourceKey normalizedKey = normalizeKey( this.resourceKey );
        doExecute( normalizedKey );
    }

    private void executeGlobal()
    {
        final ModuleResourceKey key = new ModuleResourceKey( ModuleKey.SYSTEM, ResourcePath.from( "global.js" ) );
        doExecute( key );
    }

    private Object doExecute( final ModuleResourceKey key )
    {
        final ModuleSource source = this.sourceResolver.resolve( key );
        return doExecute( source );
    }

    private Object doExecute( final ModuleSource source )
    {
        final ExecutableScript script = this.scriptEngineService.compile( source );
        return script.execute( this.bindings );
    }

    private static ModuleResourceKey normalizeKey( final ModuleResourceKey key )
    {
        if ( key.toString().endsWith( ".js" ) )
        {
            return key;
        }

        return ModuleResourceKey.from( key.toString() + ".js" );
    }
}
