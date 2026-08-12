package com.enonic.xp.lib.admin;

import java.util.Map;
import java.util.function.Supplier;

import com.enonic.xp.admin.event.AdminEventHub;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class SendToTopicHandler
    implements ScriptBean
{
    private Supplier<AdminEventHub> adminEventHub;

    private ApplicationKey applicationKey;

    private String name;

    private Map<String, Object> message = Map.of();

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setMessage( final ScriptValue message )
    {
        this.message = message != null && message.isObject() ? message.getMap() : Map.of();
    }

    public void execute()
    {
        // the caller is the bean context's application: ownership is verified by the hub
        this.adminEventHub.get().publish( this.applicationKey, this.name, this.message );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.applicationKey = context.getApplicationKey();
        this.adminEventHub = context.getService( AdminEventHub.class );
    }
}
