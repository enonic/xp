package com.enonic.xp.lib.event;

import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class EventSenderHelper
    implements ScriptBean
{
    private final static String CUSTOM_PREFIX = "custom.";

    private Supplier<EventPublisher> publisher;

    private String type;

    private boolean distributed;

    private Map<String, Object> data;

    @Override
    public void initialize( final BeanContext context )
    {
        this.publisher = context.getService( EventPublisher.class );
        this.distributed = false;
    }

    public void setType( final String type )
    {
        this.type = CUSTOM_PREFIX + type;
    }

    public void setDistributed( final boolean distributed )
    {
        this.distributed = distributed;
    }

    public void setData( final ScriptValue value )
    {
        if ( value.isObject() )
        {
            this.data = value.getMap();
        }
    }

    public void send()
    {
        final Event.Builder builder = Event.create( this.type ).
            distributed( this.distributed );

        if ( this.data != null )
        {
            for ( final Map.Entry<String, Object> entry : this.data.entrySet() )
            {
                builder.value( entry.getKey(), entry.getValue() );
            }
        }

        this.publisher.get().publish( builder.build() );
    }
}
