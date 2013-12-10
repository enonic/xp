package com.enonic.wem.portal.script.runner;

import javax.inject.Inject;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.enonic.wem.portal.script.compiler.ScriptCompiler;

public final class ScriptRunnerFactoryImpl
    implements ScriptRunnerFactory
{
    private final ScriptCompiler compiler;

    private Scriptable globalScope;

    @Inject
    public ScriptRunnerFactoryImpl( final ScriptCompiler compiler )
    {
        this.compiler = compiler;
        initialize();
    }

    private void initialize()
    {
        final Context context = Context.enter();

        try
        {
            initGlobalScope( context );
        }
        finally
        {
            Context.exit();
        }
    }

    private void initGlobalScope( final Context context )
    {
        this.globalScope = context.initStandardObjects();
    }

    private Scriptable createScope( final Context context )
    {
        final Scriptable scope = context.newObject( this.globalScope );
        scope.setPrototype( this.globalScope );
        scope.setParentScope( null );
        return scope;
    }

    @Override
    public ScriptRunner newRunner()
    {
        final Context context = Context.enter();

        try
        {
            final Scriptable scope = createScope( context );
            return new ScriptRunnerImpl( this.compiler, scope );
        }
        finally
        {
            Context.exit();
        }
    }
}
