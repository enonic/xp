package com.enonic.wem.portal.view;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.collect.Maps;

public final class ViewServiceImpl
    implements ViewService
{
    private final Map<String, ViewProcessor> processors;

    @Inject
    public ViewServiceImpl( final Set<ViewProcessor> processors )
    {
        this.processors = Maps.newHashMap();
        for ( final ViewProcessor processor : processors )
        {
            this.processors.put( processor.getName(), processor );
        }
    }

    @Override
    public String renderView( final RenderViewSpec spec )
    {
        return findProcessor( spec.getProcessor() ).process( spec );
    }

    private ViewProcessor findProcessor( final String name )
    {
        final ViewProcessor processor = this.processors.get( name );
        if ( processor != null )
        {
            return processor;
        }

        throw new ViewException( "Could not find view processor named [" + name + "]" );
    }
}
