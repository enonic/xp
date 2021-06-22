package com.enonic.xp.script.graaljs.impl.function;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.graaljs.impl.bean.BeanContextImpl;
import com.enonic.xp.script.graaljs.impl.bean.ScriptBeanFactory;
import com.enonic.xp.script.graaljs.impl.bean.ScriptBeanFactoryImpl;
import com.enonic.xp.script.graaljs.impl.executor.ScriptExecutor;
import com.enonic.xp.script.graaljs.impl.util.ScriptLogger;

public final class ScriptFunctions
{
    private final ResourceKey script;

    private final ScriptExecutor executor;

    private final ScriptBeanFactory scriptBeanFactory;

    public ScriptFunctions( final ResourceKey script, final ScriptExecutor executor )
    {
        this.script = script;
        this.executor = executor;

        final BeanContextImpl beanContext = new BeanContextImpl();
        beanContext.setExecutor( this.executor );
        beanContext.setResourceKey( this.script );

        this.scriptBeanFactory = new ScriptBeanFactoryImpl( executor.getClassLoader(), beanContext );
    }

    public ResourceKey getScript()
    {
        return this.script;
    }

    public ProxyObject getLog()
    {
        return new ScriptLogger( this.script, this.executor.getJavascriptHelper() ).asProxyObject();
    }

    public RequireFunction getRequire()
    {
        return new RequireFunction( this.script, this.executor );
    }

    public ResolveFunction getResolve()
    {
        return new ResolveFunction( this.script, this.executor );
    }

    public Application getApp()
    {
        return this.executor.getApplication();
    }

    public Object newBean( final String type )
        throws Exception
    {
        return this.scriptBeanFactory.newBean( type );
    }

    public ScriptValue toScriptValue( final Object value )
    {
        return this.executor.newScriptValue( value );
    }

    public Object toNativeObject( final Object value )
    {
        throw new UnsupportedOperationException();
    }

    public Object nullOrValue( final Object value )
    {
        return Value.asValue( value ).isNull() ? null : value;
    }

    public void registerMock( final String name, final Object value )
    {
        this.executor.registerMock( name, value );
    }

    public void disposer( final Runnable runnable )
    {
        this.executor.registerDisposer( this.script, runnable );
    }
}
