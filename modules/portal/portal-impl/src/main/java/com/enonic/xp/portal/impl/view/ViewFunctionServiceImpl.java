package com.enonic.xp.portal.impl.view;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.portal.view.ViewFunction;
import com.enonic.xp.portal.view.ViewFunctionParams;
import com.enonic.xp.portal.view.ViewFunctionService;

@Component(immediate = true)
public final class ViewFunctionServiceImpl
    implements ViewFunctionService
{
    private final Map<String, ViewFunction> functions = new ConcurrentHashMap<>();

    private ViewFunction getFunction( final String name )
    {
        final ViewFunction function = this.functions.get( name );
        if ( function != null )
        {
            return function;
        }

        throw new IllegalArgumentException( "No such view function [" + name + "]" );
    }

    @Override
    public Object execute( final ViewFunctionParams params )
    {
        return getFunction( params.getName() ).execute( params );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addFunction( final ViewFunction function )
    {
        this.functions.put( function.getName(), function );
    }

    public void removeFunction( final ViewFunction function )
    {
        this.functions.remove( function.getName() );
    }
}
