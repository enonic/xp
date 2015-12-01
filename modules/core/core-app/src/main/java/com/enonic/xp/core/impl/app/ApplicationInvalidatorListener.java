package com.enonic.xp.core.impl.app;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationEvent;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

@Component(immediate = true)
public final class ApplicationInvalidatorListener
    implements EventListener
{
    private final List<ApplicationInvalidator> list;

    public ApplicationInvalidatorListener()
    {
        this.list = Lists.newCopyOnWriteArrayList();
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( event instanceof ApplicationEvent )
        {
            onEvent( (ApplicationEvent) event );
        }
    }

    private void onEvent( final ApplicationEvent event )
    {
        invalidate( event.getKey() );
    }

    private void invalidate( final ApplicationKey key )
    {
        for ( final ApplicationInvalidator invalidator : this.list )
        {
            invalidator.invalidate( key );
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addListener( final ApplicationInvalidator invalidator )
    {
        this.list.add( invalidator );
    }

    public void removeListener( final ApplicationInvalidator invalidator )
    {
        this.list.remove( invalidator );
    }
}
