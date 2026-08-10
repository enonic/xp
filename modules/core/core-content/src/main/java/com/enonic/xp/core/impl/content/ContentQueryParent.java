package com.enonic.xp.core.impl.content;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodePath;

/**
 * The parent a {@link ContentQuery} is restricted to, resolved into the node path used to filter children and the child order used to
 * sort them. The child order is {@code null} where the query supplies its own order expressions and the order of the parent must not be
 * applied.
 */
@NullMarked
record ContentQueryParent( NodePath nodePath, @Nullable ChildOrder childOrder )
{
    /**
     * Resolves the parent named by the query, which must name one either by path or by id. Reading the parent is what makes its child
     * order available, and its path where the parent is given by id, so the read is skipped where neither is required; a query that names
     * a parent by path and sorts explicitly is therefore served by a single search.
     * <p>
     * The read is performed with elevated privileges because nothing the parent holds is exposed to the caller: the path is used only to
     * build the filter and the child order only to sort. A caller permitted to read a child but not its parent therefore receives that
     * child, and one permitted to read neither still receives nothing. Ordering by a parent the caller cannot see yields the same query
     * that the path form already answers without any read.
     *
     * @return the resolved parent, or {@code null} where the query names a parent that does not exist, in which case the query cannot
     * match anything.
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
