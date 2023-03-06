package com.enonic.xp.repo.impl.node.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.event.Event;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repository.RepositoryId;

abstract class AbstractNodeEventHandler
    implements NodeEventHandler
{
    private static final String ID = "id";

    private static final String PATH = "path";

    private static final String BRANCH = "branch";

    private static final String REPOSITORY_ID = "repo";

    static final String NEW_PATH = "newPath";

    static final String CURRENT_TARGET_PATH = "currentTargetPath";

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

    InternalContext createNodeContext( final Map<Object, Object> map, final InternalContext context )
    {
        final InternalContext.Builder nodeContext = InternalContext.create( context );

        final RepositoryId repositoryId = getRepositoryId( map );
        if ( repositoryId != null )
        {
            nodeContext.repositoryId( repositoryId );
        }

        final Branch branch = getBranch( map );
        if ( branch != null )
        {
            nodeContext.branch( branch );
        }
        return nodeContext.build();
    }

    RepositoryId getRepositoryId( final Map<Object, Object> map )
    {
        return map.containsKey( REPOSITORY_ID ) ? RepositoryId.from( map.get( REPOSITORY_ID ).toString() ) : null;
    }

    Branch getBranch( final Map<Object, Object> map )
    {
        return map.containsKey( BRANCH ) ? Branch.from( map.get( BRANCH ).toString() ) : null;
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
