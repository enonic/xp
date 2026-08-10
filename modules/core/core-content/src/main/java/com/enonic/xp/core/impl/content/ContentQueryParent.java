package com.enonic.xp.core.impl.content;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodePath;

/**
 * The parent a {@link ContentQuery} is restricted to, resolved into the node path to filter children on and the child order to sort them
 * by. The child order is {@code null} when the query brings its own order expressions and the parent's order must not be applied.
 */
@NullMarked
record ContentQueryParent( NodePath nodePath, @Nullable ChildOrder childOrder )
{
    static boolean isSpecifiedIn( final ContentQuery query )
    {
        return query.getParentPath() != null || query.getParentId() != null;
    }

    /**
     * Resolves the parent named by the query. Reading the parent is what makes its child order and, for a parent given by id, its path
     * available - so it is skipped when neither is needed, keeping a query that names a parent by path and sorts explicitly down to a
     * single search.
     * <p>
     * The read is done with elevated privileges because nothing the parent holds reaches the caller: the path only builds the filter and
     * the child order only sorts, so a caller who may read a child but not its parent gets that child either way, and one who may read
     * neither still gets nothing. Ordering by a parent nobody can see is the same query the path form already answers without any read.
     *
     * @return the resolved parent, or {@code null} when the query names a parent that does not exist, in which case the query cannot match
     * anything.
     */
    static @Nullable ContentQueryParent resolve( final ContentQuery query, final AbstractContentCommand command )
    {
        final ContentPath parentPath = query.getParentPath();
        // a parent orders its own children, so its order says nothing about a subtree - a recursive query keeps the order it asks for
        final boolean orderFromParent =
            !query.isRecursive() && ( query.getQueryExpr() == null || query.getQueryExpr().getOrderList().isEmpty() );

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

    private static @Nullable Content doGetParent( final ContentQuery query, final AbstractContentCommand command )
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
