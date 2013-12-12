package com.enonic.wem.portal.script.require;

import java.text.MessageFormat;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.TopLevel;

import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.portal.script.compiler.ScriptCompiler;
import com.enonic.wem.portal.script.loader.ScriptLoader;
import com.enonic.wem.portal.script.loader.ScriptSource;

final class RequireFunctionExecutor
{
    private Context context;

    private Scriptable scope;

    private String name;

    private ScriptLoader scriptLoader;

    private ScriptCompiler scriptCompiler;

    private ModuleResourceKeyResolver resourceKeyResolver;

    public void setContext( final Context context )
    {
        this.context = context;
    }

    public void setScope( final Scriptable scope )
    {
        this.scope = scope;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setScriptLoader( final ScriptLoader scriptLoader )
    {
        this.scriptLoader = scriptLoader;
    }

    public void setScriptCompiler( final ScriptCompiler scriptCompiler )
    {
        this.scriptCompiler = scriptCompiler;
    }

    public void setResourceKeyResolver( final ModuleResourceKeyResolver resourceKeyResolver )
    {
        this.resourceKeyResolver = resourceKeyResolver;
    }

    private ScriptSource loadScriptSource( final String name )
    {
        if ( name.contains( ":" ) )
        {
            return loadScriptFromModule( name );
        }
        else
        {
            return loadScriptFromSystem( name );
        }
    }

    private JavaScriptException error( final String message, final Object... args )
    {
        return ScriptRuntime.throwError( this.context, this.scope, MessageFormat.format( message, args ) );
    }

    private ScriptSource loadScriptFromModule( final String name )
    {
        final ModuleResourceKey key = this.resourceKeyResolver.resolve( name );
        if ( key == null )
        {
            throw error( "Could not resolve module resource from [{0}].", name );
        }

        final ScriptSource source = this.scriptLoader.loadFromModule( key );
        if ( source != null )
        {
            return source;
        }

        throw error( "Module resource [{0}] does not exist.", name );
    }

    private ScriptSource loadScriptFromSystem( final String name )
    {
        final ScriptSource source = this.scriptLoader.loadFromSystem( name );
        if ( source != null )
        {
            return source;
        }

        throw error( "System resource [{0}] does not exist.", name );
    }

    public Scriptable execute()
    {
        final ScriptSource source = loadScriptSource( this.name );
        final Script compiledScript = this.scriptCompiler.compile( this.context, source );
        return execute( compiledScript );
    }

    private Scriptable execute( final Script script )
    {
        final Scriptable exports = this.context.newObject( this.scope );
        final Scriptable moduleObject = this.context.newObject( this.scope );

        final TopLevel newScope = new TopLevel();
        newScope.setPrototype( this.scope );
        newScope.put( "exports", newScope, exports );
        newScope.put( "module", newScope, moduleObject );
        moduleObject.put( "exports", moduleObject, exports );

        script.exec( this.context, newScope );
        return exports;
    }
}
