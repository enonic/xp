package com.enonic.wem.portal.script.runner;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

import com.google.common.collect.Maps;

import com.enonic.wem.portal.script.compiler.ScriptCompiler;
import com.enonic.wem.portal.script.loader.ScriptSource;

final class ScriptRunnerImpl
    implements ScriptRunner
{
    private final Scriptable scope;

    private final ScriptCompiler compiler;

    private final Map<String, Object> objects;

    private ScriptSource source;

    public ScriptRunnerImpl( final ScriptCompiler compiler, final Scriptable scope )
    {
        this.compiler = compiler;
        this.scope = scope;
        this.objects = Maps.newHashMap();
    }

    @Override
    public ScriptRunner source( final ScriptSource source )
    {
        this.source = source;
        return this;
    }

    @Override
    public ScriptRunner property( final String name, final Object value )
    {
        this.objects.put( name, value );
        return this;
    }

    @Override
    public void execute()
    {
        final Context context = Context.enter();

        try
        {
            setObjectsToScope();
            final Script script = this.compiler.compile( context, this.source );
            script.exec( context, this.scope );
        }
        finally
        {
            Context.exit();
        }
    }

    private void setObjectsToScope()
    {
        for ( final Map.Entry<String, Object> entry : this.objects.entrySet() )
        {
            this.scope.put( entry.getKey(), this.scope, Context.javaToJS( entry.getValue(), this.scope ) );
        }
    }
}
