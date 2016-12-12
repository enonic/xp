package com.enonic.xp.lib.event;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.script.event.ScriptEventListenerBuilder;
import com.enonic.xp.script.event.ScriptEventManager;

public final class EventListenerHelper
    implements ScriptBean
{
    private Supplier<ScriptEventManager> manager;

    private ScriptEventListenerBuilder builder;

    @Override
    public void initialize( final BeanContext context )
    {
        this.manager = context.getService( ScriptEventManager.class );
        this.builder = new ScriptEventListenerBuilder();
        this.builder.application( context.getApplicationKey() );
    }

    public void setType( final String pattern )
    {
        this.builder.typePattern( pattern );
    }

    public void setListener( final Consumer<Object> callback )
    {
        this.builder.listener( callback );
    }

    public void setLocalOnly( final boolean localOnly )
    {
        this.builder.localOnly( localOnly );
    }

    public void register()
    {
        this.manager.get().add( this.builder.build() );
    }
}
