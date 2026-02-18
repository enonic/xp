package com.enonic.xp.lib.sse;

import com.enonic.xp.portal.sse.SseManager;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class SseManagerBean
    implements ScriptBean
{
    private SseManager sseManager;

    public void send( final String id, final String event, final String data, final String eventId )
    {
        this.sseManager.send( id, event, data, eventId );
    }

    public void sendToGroup( final String group, final String event, final String data )
    {
        this.sseManager.sendToGroup( group, event, data );
    }

    public void close( final String id )
    {
        this.sseManager.close( id );
    }

    public void addToGroup( final String group, final String id )
    {
        this.sseManager.addToGroup( group, id );
    }

    public void removeFromGroup( final String group, final String id )
    {
        this.sseManager.removeFromGroup( group, id );
    }

    public long getGroupSize( final String group )
    {
        return this.sseManager.getGroupSize( group );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.sseManager = context.getService( SseManager.class ).get();
    }
}
