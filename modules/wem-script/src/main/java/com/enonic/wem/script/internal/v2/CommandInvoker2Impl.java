package com.enonic.wem.script.internal.v2;

import java.util.Map;

import com.google.common.collect.Maps;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.enonic.wem.script.v2.CommandHandler2;
import com.enonic.wem.script.v2.CommandInvoker2;

public final class CommandInvoker2Impl
    implements CommandInvoker2
{
    private final Map<String, CommandHandler2> handlers;

    private final BeanParamConverter beanParamConverter;

    private final ScriptObjectConverter scriptObjectConverter;

    private final CommandResultConverter commandResultConverter;

    public CommandInvoker2Impl()
    {
        this.handlers = Maps.newHashMap();
        this.beanParamConverter = new BeanParamConverter();
        this.scriptObjectConverter = new ScriptObjectConverter();
        this.commandResultConverter = new CommandResultConverter();
    }

    public void register( final CommandHandler2 handler )
    {
        this.handlers.put( handler.getName(), handler );
    }

    public void unregister( final CommandHandler2 handler )
    {
        this.handlers.remove( handler.getName() );
    }

    @Override
    public Object invoke( final String name, final ScriptObjectMirror input )
    {
        final CommandHandler2 handler = this.handlers.get( name );
        if ( handler != null )
        {
            return invoke( handler, input );
        }

        throw new IllegalArgumentException( String.format( "Command [%s] not found", name ) );
    }

    private Object invoke( final CommandHandler2 handler, final ScriptObjectMirror input )
    {
        final Map<String, Object> inputMap = this.scriptObjectConverter.toMap( input );

        final Object inputBean = handler.createInputBean();
        this.beanParamConverter.setProperties( inputBean, inputMap );

        final Object result = doInvoke( handler, inputBean );
        return this.commandResultConverter.toResult( result );
    }

    @SuppressWarnings("unchecked")
    private Object doInvoke( final CommandHandler2 handler, final Object input )
    {
        return handler.execute( input );
    }
}
