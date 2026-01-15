package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.content.WorkflowInfo;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class WorkflowInfoMapper
    implements MapSerializable
{
    private final WorkflowInfo value;

    public WorkflowInfoMapper( final WorkflowInfo value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        if ( value != null )
        {
            gen.value( "state", value.getState().toString() );
            gen.end();
        }
    }
}
