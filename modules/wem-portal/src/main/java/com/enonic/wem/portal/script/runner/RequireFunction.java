package com.enonic.wem.portal.script.runner;

import java.text.MessageFormat;
import java.util.Map;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;

import com.google.common.collect.Maps;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.portal.script.compiler.ScriptCompiler;
import com.enonic.wem.portal.script.loader.ScriptLoader;
import com.enonic.wem.portal.script.loader.ScriptSource;

final class RequireFunction
    extends BaseFunction
{
    private final static String MODULE_PREFIX = ":";

    private final Map<String, Scriptable> exportMap;

    private ScriptLoader scriptLoader;

    private ScriptCompiler scriptCompiler;

    private ScriptSource mainSource;

    public RequireFunction()
    {
        this.exportMap = Maps.newHashMap();
    }

    public void setSource( final ScriptSource mainSource )
    {
        this.mainSource = mainSource;
    }

    public void setScriptLoader( final ScriptLoader scriptLoader )
    {
        this.scriptLoader = scriptLoader;
    }

    public void setScriptCompiler( final ScriptCompiler scriptCompiler )
    {
        this.scriptCompiler = scriptCompiler;
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
        throw error( cx, scope, "require() can not be invoked as a constructor" );
    }

    @Override
    public Object call( final Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args )
    {
        if ( ( args == null ) || ( args.length < 1 ) )
        {
            throw error( cx, scope, "require() needs one argument" );
        }

        final String name = (String) Context.jsToJava( args[0], String.class );
        final ScriptSource source = resolveSource( cx, scope, name );

        final Scriptable exports = this.exportMap.get( source.getName() );
        if ( exports != null )
        {
            return exports;
        }

        return doCall( cx, scope, source );
    }

    private ScriptSource resolveSource( final Context cx, final Scriptable scope, final String name )
    {
        if ( !name.endsWith( ".js" ) )
        {
            return resolveSource( cx, scope, name + ".js" );
        }

        if ( name.startsWith( MODULE_PREFIX ) )
        {
            return resolveFromModule( cx, scope, name.substring( MODULE_PREFIX.length() ) );
        }

        return resolveFromSystem( cx, scope, name );
    }

    private ScriptSource resolveFromModule( final Context cx, final Scriptable scope, final String name )
    {
        final ModuleKey currentModule = this.mainSource.getModule();
        final ModuleResourceKey key = new ModuleResourceKey( currentModule, ResourcePath.from( name ) );

        final ScriptSource source = this.scriptLoader.loadFromModule( key );
        if ( source != null )
        {
            return source;
        }

        throw error( cx, scope, "Could not find resource [{0}].", key );
    }

    private ScriptSource resolveFromSystem( final Context cx, final Scriptable scope, final String name )
    {
        final ScriptSource source = this.scriptLoader.loadFromSystem( name );
        if ( source != null )
        {
            return source;
        }

        throw error( cx, scope, "Could not find resource [{0}] from system.", name );
    }

    private Object doCall( final Context cx, final Scriptable scope, final ScriptSource source )
    {
        final Script script = this.scriptCompiler.compile( cx, source );

        final Scriptable exports = cx.newObject( scope );
        final Scriptable moduleObject = cx.newObject( scope );

        final TopLevel newScope = new TopLevel();
        newScope.setPrototype( scope );
        newScope.put( "exports", newScope, exports );
        newScope.put( "module", newScope, moduleObject );
        moduleObject.put( "exports", moduleObject, exports );

        script.exec( cx, newScope );

        final Scriptable newExports = ScriptRuntime.toObject( scope, exports );
        this.exportMap.put( source.getName(), newExports );
        return newExports;
    }

    public void install( final Scriptable scope )
    {
        ScriptableObject.putProperty( scope, "require", this );
    }

    private JavaScriptException error( final Context context, final Scriptable scope, final String message, final Object... args )
    {
        return ScriptRuntime.throwError( context, scope, MessageFormat.format( message, args ) );
    }
}
