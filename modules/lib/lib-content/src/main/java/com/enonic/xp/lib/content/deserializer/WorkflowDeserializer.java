package com.enonic.xp.lib.content.deserializer;

import java.util.Map;

import com.enonic.xp.content.WorkflowInfo;

public final class WorkflowDeserializer
{

    public WorkflowInfo deserialize( Map<String, Object> map )
    {
        if ( map == null || map.isEmpty() )
        {
            return null;
        }

        String state = (String) map.get( "state" );

        return WorkflowInfo.create().state( state ).build();
    }
}
