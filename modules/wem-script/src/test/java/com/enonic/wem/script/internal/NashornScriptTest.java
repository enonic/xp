package com.enonic.wem.script.internal;

import java.net.URL;

import javax.script.Bindings;
import javax.script.ScriptEngine;

import org.junit.Before;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

public abstract class NashornScriptTest
{
    private ScriptEngine engine;

    private Bindings bindings;

    @Before
    public final void setup()
        throws Exception
    {
        this.engine = new NashornScriptEngineFactory().getScriptEngine();
        this.bindings = this.engine.createBindings();
        configure( this.bindings );
    }

    protected abstract void configure( Bindings bindings );

    protected final Object execute( final String name )
        throws Exception
    {
        final URL url = getClass().getResource( getClass().getSimpleName() + "-" + name + ".js" );
        final String script = Resources.toString( url, Charsets.UTF_8 );
        return eval( script );
    }

    protected final Object eval( final String script )
        throws Exception
    {
        return this.engine.eval( script, this.bindings );
    }
}
