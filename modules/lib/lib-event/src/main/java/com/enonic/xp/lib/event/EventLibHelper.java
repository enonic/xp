package com.enonic.xp.lib.event;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.script.event.ScriptEventListener;
import com.enonic.xp.script.event.ScriptEventListenerBuilder;
import com.enonic.xp.script.event.ScriptEventManager;

public final class EventLibHelper
    implements ScriptBean
{
    private BeanContext context;

    private Supplier<ScriptEventManager> manager;

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context;
        this.manager = this.context.getService( ScriptEventManager.class );
    }

    public void listener( final String pattern, final Consumer<Object> callback )
    {
        final ScriptEventListener listener = new ScriptEventListenerBuilder().
            application( this.context.getApplicationKey() ).
            pattern( pattern ).
            listener( callback ).
            build();

        this.manager.get().add( listener );
    }
}
