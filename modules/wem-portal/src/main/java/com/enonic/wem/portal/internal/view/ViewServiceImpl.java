package com.enonic.wem.portal.internal.view;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Maps;

import com.enonic.wem.portal.view.ViewModel;
import com.enonic.wem.portal.view.ViewProcessor;
import com.enonic.wem.portal.view.ViewService;

@Component(immediate = true)
public final class ViewServiceImpl
    implements ViewService
{
    private final Map<String, ViewProcessor> processors;

    public ViewServiceImpl()
    {
        this.processors = Maps.newHashMap();
    }

    private ViewProcessor findProcessor( final ViewModel viewModel )
    {
        final ViewProcessor processor = this.processors.get( viewModel.getName() );
        if ( processor != null )
        {
            return processor;
        }

        throw new IllegalArgumentException( String.format( "View processor [%s] not found", viewModel.getName() ) );
    }

    @Override
    public String process( final ViewModel viewModel )
    {
        return findProcessor( viewModel ).process( viewModel );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addProcessor( final ViewProcessor processor )
    {
        this.processors.put( processor.getName(), processor );
    }

    public void removeProcessor( final ViewProcessor processor )
    {
        this.processors.remove( processor.getName() );
    }
}
