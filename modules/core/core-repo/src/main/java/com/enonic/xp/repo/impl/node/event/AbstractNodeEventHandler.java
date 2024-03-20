package com.enonic.xp.repo.impl.node.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.enonic.xp.event.Event;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

abstract class AbstractNodeEventHandler
    implements NodeEventHandler
{
    private static final String ID = "id";

    private static final String PATH = "path";

    @SuppressWarnings("unchecked")
    List<Map<Object, Object>> getValueMapList( final Event event )
    {
        try
        {
            final List nodesList = event.getValueAs( List.class, "nodes" ).get();
            return doGetValueMap( nodesList );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( "Unexpected format on event, expected 'nodes'-list", e );
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<Object, Object>> doGetValueMap( final List<Object> nodesList )
    {
        final List<Map<Object, Object>> mapList = new ArrayList<>();

        for ( final Object listItem : nodesList )
        {
            if ( !( listItem instanceof Map ) )
            {
                throw new IllegalArgumentException( "Unexpected format on event, expected nodes-list do be list of Map<String, String>" );
            }

            mapList.add( (Map) listItem );
        }

        return mapList;
    }

    NodePath getPath( final Map<Object, Object> map )
    {
        return new NodePath( map.get( PATH ).toString() );
    }

    NodeId getId( final Map<Object, Object> map )
    {
        return NodeId.from( map.get( ID ) );
    }
}
