package com.enonic.xp.portal.impl.script.invoker;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Maps;

import com.enonic.xp.portal.impl.script.bean.JsObjectConverter;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;

@Component(immediate = true)
public final class CommandInvokerImpl
    implements CommandInvoker
{
    private final Map<String, CommandHandler> handlers;

    public CommandInvokerImpl()
    {
        this.handlers = Maps.newConcurrentMap();
    }

    @Override
    public Object invoke( final CommandRequest req )
    {
        final String name = req.getName();
        final CommandHandler handler = this.handlers.get( name );
        if ( handler != null )
        {
            return invoke( handler, req );
        }

        throw new IllegalArgumentException( String.format( "Command [%s] not found", name ) );
    }

    private Object invoke( final CommandHandler handler, final CommandRequest req )
    {
        final Object result = handler.execute( req );
        return JsObjectConverter.toJs( result );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addHandler( final CommandHandler handler )
    {
        this.handlers.put( handler.getName(), handler );
    }

    public void removeHandler( final CommandHandler handler )
    {
        this.handlers.remove( handler.getName() );
    }
}
