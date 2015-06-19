package com.enonic.xp.portal.impl.script.function;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.bean.ScriptBean;
import com.enonic.xp.portal.impl.script.ScriptExecutor;
import com.enonic.xp.portal.impl.script.bean.JsObjectConverter;
import com.enonic.xp.portal.impl.script.bean2.BeanContextImpl;
import com.enonic.xp.portal.impl.script.logger.ScriptLogger;
import com.enonic.xp.portal.impl.script.util.NashornHelper;
import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.resource.ResourceKey;

public final class ScriptFunctions
{
    private final ResourceKey script;

    private final ScriptExecutor executor;

    private final BeanContextImpl beanContext;

    public ScriptFunctions( final ResourceKey script, final ScriptExecutor executor )
    {
        this.script = script;
        this.executor = executor;
        this.beanContext = new BeanContextImpl();
        this.beanContext.setExecutor( this.executor );
        this.beanContext.setResource( this.script );
    }

    public ResourceKey getScript()
    {
        return this.script;
    }

    public ModuleKey getModule()
    {
        return this.script.getModule();
    }

    public ScriptLogger getLog()
    {
        return new ScriptLogger( this.script );
    }

    public ExecuteFunction getExecute()
    {
        return new ExecuteFunction( this.script, this.executor );
    }

    public RequireFunction getRequire()
    {
        return new RequireFunction( this.script, this.executor );
    }

    public ResolveFunction getResolve()
    {
        return new ResolveFunction( this.script );
    }

    public Object newBean( final String type )
        throws Exception
    {
        final Class<?> clz = Class.forName( type, true, this.executor.getClassLoader() );
        final Object instance = clz.newInstance();

        injectBean( instance );
        return instance;
    }

    private void injectBean( final Object instance )
    {
        if ( instance instanceof ScriptBean )
        {
            ( (ScriptBean) instance ).initialize( this.beanContext );
        }
    }

    public ScriptValue toScriptValue( final Object value )
    {
        return this.executor.newScriptValue( value );
    }

    public Object toNativeObject( final Object value )
    {
        return JsObjectConverter.toJs( value );
    }

    public Object nullOrValue( final Object value )
    {
        return NashornHelper.isUndefined( value ) ? null : value;
    }
}
