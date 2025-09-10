package com.enonic.xp.lib.content.deserializer;

import java.util.Map;
import java.util.stream.Collectors;

import com.enonic.xp.content.WorkflowCheckState;
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
        Map<String, WorkflowCheckState> checks = null;

        if ( map.containsKey( "checks" ) )
        {
            checks = ( (Map<String, String>) map.get( "checks" ) ).entrySet()
                .stream()
                .collect( Collectors.toMap( Map.Entry::getKey, e -> WorkflowCheckState.valueOf( e.getValue() ) ) );
        }

        return WorkflowInfo.create().state( state ).checks( checks != null ? checks : Map.of() ).build();
    }
}
