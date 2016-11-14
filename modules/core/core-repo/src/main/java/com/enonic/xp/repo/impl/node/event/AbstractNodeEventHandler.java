package com.enonic.xp.repo.impl.node.event;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.event.Event;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;

abstract class AbstractNodeEventHandler
    implements NodeEventHandler
{
    private final static String ID = "id";

    private final static String PATH = "path";

    private final static String BRANCH = "branch";

    final static String NEW_PATH = "newPath";

    final static String CURRENT_TARGET_PATH = "currentTargetPath";

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
        final List<Map<Object, Object>> mapList = Lists.newArrayList();

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

    InternalContext createNodeContext( final Map<Object, Object> map, final InternalContext context )
    {
        final Branch branch = getBranch( map );
        if ( branch != null && !branch.equals( context.getBranch() ) )
        {
            return InternalContext.create( context ).
                branch( branch ).
                build();
        }
        return context;
    }

    Branch getBranch( final Map<Object, Object> map )
    {
        return map.get( BRANCH ) == null ? null : Branch.from( map.get( BRANCH ).toString() );
    }

    NodePath getPath( final Map<Object, Object> map )
    {
        return NodePath.create( map.get( PATH ).toString() ).build();
    }

    NodeId getId( final Map<Object, Object> map )
    {
        return NodeId.from( map.get( ID ).toString() );
    }


}
