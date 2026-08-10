package com.enonic.xp.lib.node;

import com.enonic.xp.lib.node.mapper.NodeQueryResultMapper;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;

@SuppressWarnings("unused")
public final class FindNodesByQueryHandler
    extends AbstractFindNodesQueryHandler
{

    private FindNodesByQueryHandler( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Object execute()
    {
        NodeQuery nodeQuery = createNodeQuery();

        if ( getParent() != null )
        {
            final NodePath parentPath = resolveParentPath();

            if ( parentPath == null )
            {
                // a parent that does not exist matches nothing, the same way a parent given as a path that is not there does
                return convert( FindNodesByQueryResult.create().build() );
            }

            nodeQuery = NodeQuery.create( nodeQuery ).parent( parentPath ).recursive( isRecursive() ).build();
        }
        else if ( isRecursive() )
        {
            // recursive widens a parent restriction, so without a parent it would silently do nothing
            throw new IllegalArgumentException( "recursive expects a parent" );
        }

        final FindNodesByQueryResult result = nodeService.findByQuery( nodeQuery );

        return convert( result );
    }

    /**
     * The {@code parent} parameter is a key: a value that starts with {@code /} is a path, anything else is an id that has to be resolved
     * to its path first. An unknown id resolves to {@code null}.
     */
    private NodePath resolveParentPath()
    {
        final NodeKey parentKey = NodeKey.from( getParent() );

        if ( parentKey.isPath() )
        {
            return parentKey.getAsPath();
        }

        try
        {
            final Node parentNode = nodeService.getById( parentKey.getAsNodeId() );
            return parentNode.path();
        }
        catch ( NodeNotFoundException e )
        {
            return null;
        }
    }

    private NodeQueryResultMapper convert( final FindNodesByQueryResult findQueryResult )
    {
        return new NodeQueryResultMapper( findQueryResult, getReturns() );
    }

    public static final class Builder
        extends AbstractFindNodesQueryHandler.Builder<Builder>
    {
        public FindNodesByQueryHandler build()
        {
            return new FindNodesByQueryHandler( this );
        }
    }
}
