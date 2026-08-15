package com.enonic.xp.lib.admin;

import java.util.function.Supplier;

import com.enonic.xp.admin.event.AdminEventHub;
import com.enonic.xp.admin.event.PublishMessageParams;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.util.GenericValue;

public final class SendToTopicHandler
    implements ScriptBean
{
    private static final GenericValue EMPTY_MESSAGE = GenericValue.newObject().build();

    private Supplier<AdminEventHub> adminEventHub;

    private ApplicationKey applicationKey;

    private String name;

    private ScriptValue message;

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setMessage( final ScriptValue message )
    {
        this.message = message;
    }

    public void execute()
    {
        // the caller is the bean context's application: ownership is verified by the hub
        this.adminEventHub.get()
            .publish( PublishMessageParams.create()
                          .caller( this.applicationKey )
                          .name( this.name )
                          .message( toMessage() )
                          .build() );
    }

    private GenericValue toMessage()
    {
        if ( this.message == null )
        {
            return EMPTY_MESSAGE;
        }
        try
        {
            // isObject, isArray and isValue are exclusive: an array is not an object here
            if ( this.message.isObject() )
            {
                return GenericValue.fromRawJava( this.message.getMap() );
            }
            if ( this.message.isArray() )
            {
                return GenericValue.fromRawJava( this.message.getList() );
            }
            if ( this.message.isValue() )
            {
                return GenericValue.fromRawJava( this.message.getValue() );
            }
        }
        catch ( RuntimeException e )
        {
            throw new IllegalArgumentException( "Message for topic [" + this.name + "] contains null or unsupported values", e );
        }
        throw new IllegalArgumentException( "Message for topic [" + this.name + "] must be an object, an array or a primitive value" );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.applicationKey = context.getApplicationKey();
        this.adminEventHub = context.getService( AdminEventHub.class );
    }
}
