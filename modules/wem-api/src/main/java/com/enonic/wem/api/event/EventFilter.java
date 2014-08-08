package com.enonic.wem.api.event;

public final class EventFilter<E extends Event>
    implements EventListener
{
    private final Class<E> eventType;

    private final FilteredEventListener<E> filteredEventListener;

    private EventFilter( final Class<E> eventType, final FilteredEventListener<E> onEvent )
    {
        this.eventType = eventType;
        this.filteredEventListener = onEvent;
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( eventType.isInstance( event ) )
        {
            this.filteredEventListener.onEvent( eventType.cast( event ) );
        }
    }

    public static <E extends Event> EventFilter<E> filterOn( final Class<E> eventType, final FilteredEventListener<E> onEvent )
    {
        return new EventFilter<>( eventType, onEvent );
    }
}
