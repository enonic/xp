package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodePath;

/**
 * The parent a {@link ContentQuery} is restricted to, resolved into the node path to filter children on and the child order to sort them
 * by. The child order is {@code null} when the query brings its own order expressions and the parent's order must not be applied.
 */
record ContentQueryParent( NodePath nodePath, ChildOrder childOrder )
{
    static boolean isSpecifiedIn( final ContentQuery query )
    {
        return query.getParentPath() != null || query.getParentId() != null;
    }

    /**
     * Resolves the parent named by the query. Reading the parent is what makes its child order and, for a parent given by id, its path
     * available - so it is skipped when neither is needed, keeping a query that names a parent by path and sorts explicitly down to a
     * single search. The read is done with elevated privileges: which order children come back in is not something to hide from a caller
     * that is allowed to see them, and the children themselves stay subject to the permissions of the caller.
     *
     * @return the resolved parent, or {@code null} when the query names a parent that does not exist, in which case the query cannot match
     * anything.
     */
    static ContentQueryParent resolve( final ContentQuery query, final AbstractContentCommand command )
    {
        final ContentPath parentPath = query.getParentPath();
        final boolean orderFromParent = query.getQueryExpr() == null || query.getQueryExpr().getOrderList().isEmpty();

        if ( parentPath != null && !orderFromParent )
        {
            return new ContentQueryParent( ContentNodeHelper.translateContentPathToNodePath( parentPath ), null );
        }

        final Content parent = command.runAsAdmin( () -> doGetParent( query, command ) );

        if ( parent == null )
        {
            return null;
        }

        return new ContentQueryParent( ContentNodeHelper.translateContentPathToNodePath( parent.getPath() ), orderFromParent
            ? ContentChildOrder.withLanguage( parent.getChildOrder(), parent.getLanguage() )
            : null );
    }

    private static Content doGetParent( final ContentQuery query, final AbstractContentCommand command )
    {
        final ContentPath parentPath = query.getParentPath();

        if ( parentPath == null )
        {
            return GetContentByIdCommand.create( query.getParentId(), command ).build().execute();
        }
        else if ( parentPath.isRoot() )
        {
            return GetContentByPathCommand.create( ContentPath.ROOT, command ).allowRoot().build().execute();
        }
        else
        {
            return GetContentByPathCommand.create( parentPath, command ).build().execute();
        }
    }
}
