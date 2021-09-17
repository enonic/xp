package com.enonic.xp.script.impl.function;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.bean.BeanContextImpl;
import com.enonic.xp.script.impl.bean.ScriptBeanFactory;
import com.enonic.xp.script.impl.bean.ScriptBeanFactoryImpl;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.util.JsObjectConverter;
import com.enonic.xp.script.impl.util.ScriptLogger;

public final class ScriptFunctions
{
    private final Context context;

    private final ResourceKey script;

    private final ScriptExecutor executor;

    private final ScriptBeanFactory scriptBeanFactory;

    private final JsObjectConverter converter;

    private final ScriptLogger logger;

    public ScriptFunctions( final Context context, final ResourceKey script, final ScriptExecutor executor )
    {
        this.context = context;
        this.script = script;
        this.executor = executor;

        final BeanContextImpl beanContext = new BeanContextImpl();
        beanContext.setExecutor( this.executor );
        beanContext.setResourceKey( this.script );

        this.scriptBeanFactory = new ScriptBeanFactoryImpl( executor.getClassLoader(), beanContext );
        this.converter = new JsObjectConverter( this.executor.getJavascriptHelper() );
        this.logger = new ScriptLogger( this.context, this.script, this.executor.getJavascriptHelper() );
    }

    public ResourceKey getScript()
    {
        return this.script;
    }

    public ProxyObject getLog()
    {
        synchronized ( context )
        {
            return this.logger.asProxyObject();
        }
    }

    public RequireFunction getRequire()
    {
        return new RequireFunction( context, this.script, this.executor );
    }

    public ResolveFunction getResolve()
    {
        return new ResolveFunction( context, this.script, this.executor );
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
        synchronized ( context )
        {
            return this.converter.toJs( value );
        }
    }

    public Object nullOrValue( final Object value )
    {
        synchronized ( context )
        {
            return Value.asValue( value ).isNull() ? null : value;
        }
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
