package com.enonic.xp.script.impl.event;

import java.util.Iterator;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.script.event.ScriptEventListener;
import com.enonic.xp.script.event.ScriptEventManager;

@Component(immediate = true, service = {ScriptEventManager.class, ApplicationInvalidator.class, EventListener.class})
public final class ScriptEventManagerImpl
    implements ScriptEventManager, ApplicationInvalidator, EventListener
{
    private final Multimap<ApplicationKey, ScriptEventListener> listeners;

    public ScriptEventManagerImpl()
    {
        this.listeners = HashMultimap.create();
    }

    @Override
    public void add( final ScriptEventListener listener )
    {
        this.listeners.put( listener.getApplication(), listener );
    }

    @Override
    public Iterator<ScriptEventListener> iterator()
    {
        return Lists.newArrayList( this.listeners.values() ).iterator();
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
        this.listeners.removeAll( key );
    }

    @Override
    public void onEvent( final Event event )
    {
        for ( final ScriptEventListener listener : this )
        {
            listener.onEvent( event );
        }
    }
}
