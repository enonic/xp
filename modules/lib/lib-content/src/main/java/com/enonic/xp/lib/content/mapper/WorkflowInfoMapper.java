package com.enonic.xp.lib.content.mapper;

import java.util.Map;

import com.enonic.xp.content.WorkflowCheckState;
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
            gen.map( "checks" );
            for ( Map.Entry<String, WorkflowCheckState> e : value.getChecks().entrySet() )
            {
                gen.value( e.getKey(), e.getValue().toString() );
            }
            gen.end();
        }
    }
}
