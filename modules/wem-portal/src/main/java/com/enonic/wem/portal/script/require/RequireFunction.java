package com.enonic.wem.portal.script.require;

import java.util.Map;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.google.common.collect.Maps;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.core.module.ModuleKeyResolver;
import com.enonic.wem.portal.script.compiler.ScriptCompiler;
import com.enonic.wem.portal.script.loader.ScriptLoader;
import com.enonic.wem.portal.script.loader.ScriptSource;

public final class RequireFunction
    extends BaseFunction
{
    private final Map<String, Scriptable> exportMap;

    private ScriptSource source;

    private ScriptLoader scriptLoader;

    private ScriptCompiler scriptCompiler;

    private ModuleKeyResolver moduleKeyResolver;

    public RequireFunction()
    {
        this.exportMap = Maps.newHashMap();
    }

    public void setSource( final ScriptSource source )
    {
        this.source = source;
    }

    public void setScriptLoader( final ScriptLoader scriptLoader )
    {
        this.scriptLoader = scriptLoader;
    }

    public void setScriptCompiler( final ScriptCompiler scriptCompiler )
    {
        this.scriptCompiler = scriptCompiler;
    }

    public void setModuleKeyResolver( final ModuleKeyResolver moduleKeyResolver )
    {
        this.moduleKeyResolver = moduleKeyResolver;
    }

    @Override
    public String getFunctionName()
    {
        return "require";
    }

    @Override
    public int getArity()
    {
        return 1;
    }

    @Override
    public int getLength()
    {
        return 1;
    }

    @Override
    public Scriptable construct( final Context cx, final Scriptable scope, final Object[] args )
    {
        throw ScriptRuntime.throwError( cx, scope, "require() can not be invoked as a constructor" );
    }

    @Override
    public Object call( final Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args )
    {
        if ( ( args == null ) || ( args.length < 1 ) )
        {
            throw ScriptRuntime.throwError( cx, scope, "require() needs one argument" );
        }

        final String name = (String) Context.jsToJava( args[0], String.class );
        final Scriptable exports = this.exportMap.get( name );
        if ( exports != null )
        {
            return exports;
        }

        final RequireFunctionExecutor executor = new RequireFunctionExecutor();
        executor.setContext( cx );
        executor.setName( name );
        executor.setScope( scope );
        executor.setScriptLoader( this.scriptLoader );
        executor.setScriptCompiler( this.scriptCompiler );

        final ModuleResourceKey currentResource = this.source.getResourceKey();
        final ModuleKey currentModule = currentResource != null ? currentResource.getModuleKey() : null;

        executor.setResourceKeyResolver( new ModuleResourceKeyResolver( this.moduleKeyResolver, currentModule ) );

        final Scriptable newExports = executor.execute();
        this.exportMap.put( name, newExports );
        return newExports;
    }

    public void install( final Scriptable scope )
    {
        ScriptableObject.putProperty( scope, "require", this );
    }
}
