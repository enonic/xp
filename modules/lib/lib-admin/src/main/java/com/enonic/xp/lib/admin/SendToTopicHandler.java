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
    private Supplier<AdminEventHub> adminEventHub;

    private ApplicationKey applicationKey;

    private String name;

    private GenericValue message = GenericValue.newObject().build();

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setMessage( final ScriptValue message )
    {
        this.message =
            message != null && message.isObject() ? GenericValue.fromRawJava( message.getMap() ) : GenericValue.newObject().build();
    }

    public void execute()
    {
        // the caller is the bean context's application: ownership is verified by the hub
        this.adminEventHub.get()
            .publish( PublishMessageParams.create()
                          .caller( this.applicationKey )
                          .name( this.name )
                          .message( this.message )
                          .build() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.applicationKey = context.getApplicationKey();
        this.adminEventHub = context.getService( AdminEventHub.class );
    }
}
