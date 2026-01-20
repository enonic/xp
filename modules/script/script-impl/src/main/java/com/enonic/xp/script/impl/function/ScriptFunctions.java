package com.enonic.xp.script.impl.function;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.bean.BeanContextImpl;
import com.enonic.xp.script.impl.bean.ScriptBeanFactory;
import com.enonic.xp.script.impl.bean.ScriptBeanFactoryImpl;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.util.NashornHelper;
import com.enonic.xp.script.impl.util.ObjectConverter;
import com.enonic.xp.script.impl.util.ScriptLogger;

public final class ScriptFunctions
{
    private final ResourceKey script;

    private final ScriptExecutor executor;

    private final ScriptBeanFactory scriptBeanFactory;

    private final ObjectConverter converter;

    private final ScriptLogger logger;

    public ScriptFunctions( final ResourceKey script, final ScriptExecutor executor )
    {
        this.script = script;
        this.executor = executor;

        final BeanContextImpl beanContext = new BeanContextImpl();
        beanContext.setExecutor( this.executor );
        beanContext.setResourceKey( this.script );

        this.scriptBeanFactory = new ScriptBeanFactoryImpl( this.executor.getClassLoader(), beanContext );
        this.converter = this.executor.getObjectConverter();
        this.logger = new ScriptLogger( this.script, this.executor.getObjectConverter() );
    }

    public ResourceKey getScript()
    {
        return this.script;
    }

    public ScriptLogger getLog()
    {
        return this.logger;
    }

    public RequireFunction getRequire()
    {
        return new RequireFunction( this.script, this.executor );
    }

    public ResolveFunction getResolve()
    {
        return new ResolveFunction( this.script, this.executor );
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
        return this.converter.toJs( value );
    }

    public Object nullOrValue( final Object value )
    {
        return NashornHelper.isUndefined( value ) ? null : value;
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
