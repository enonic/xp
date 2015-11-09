package com.enonic.xp.repo.impl.node.event;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import com.enonic.xp.event.Event2;

class NodesEventData
{
    private final List<NodeEventData> nodeEventDataList;

    private NodesEventData( final List<NodeEventData> nodeEventDataList )
    {
        this.nodeEventDataList = nodeEventDataList;
    }

    @SuppressWarnings("unchecked")
    static NodesEventData create( final Event2 event )
    {
        final Object nodesList = event.getData().get( "nodes" );

        if ( nodesList == null )
        {
            throw new IllegalArgumentException( "Unexpected format on event, expected 'nodes'-list" );
        }

        if ( !( nodesList instanceof Collection ) )
        {
            throw new IllegalArgumentException( "Unexpected format on event, expected 'nodes'-list, got " + nodesList.getClass() );
        }

        return new NodesEventData( handleList( (List) nodesList ) );

    }

    @SuppressWarnings("unchecked")
    private static List<NodeEventData> handleList( final List<Object> nodesList )
    {
        final List<NodeEventData> nodeEventData = Lists.newArrayList();

        for ( final Object listItem : nodesList )
        {
            if ( !( listItem instanceof Map ) )
            {
                throw new IllegalArgumentException( "Unexpected format on event, expected nodes-list do be list of Map<String, String>" );
            }

            nodeEventData.add( NodeEventData.create( (Map<String, String>) listItem ) );

        }

        return nodeEventData;
    }

    public List<NodeEventData> getNodeEventDataList()
    {
        return nodeEventDataList;
    }
}
