package com.enonic.xp.node;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.index.IndexPath;

public final class NodeQuery
    extends AbstractQuery
{
    public static final int ALL_RESULTS_SIZE_FLAG = -1;

    private final NodePath parent;

    private final boolean recursive;

    private final boolean withPath;

    private final ImmutableSet<IndexPath> returnFields;

    private NodeQuery( final Builder builder )
    {
        super( builder );
        Preconditions.checkArgument( !builder.recursive || builder.parent != null, "recursive expects a parent" );
        this.parent = builder.parent;
        this.recursive = builder.recursive;
        this.withPath = builder.withPath;
        this.returnFields = ImmutableSet.copyOf( builder.returnFields );
    }

    public NodePath getParent()
    {
        return parent;
    }

    /**
     * Whether the parent restriction reaches every descendant instead of the direct children only. Always {@code false} when the query is
     * not restricted to a parent.
     *
     * @since 8.1.0
     */
    public boolean isRecursive()
    {
        return recursive;
    }

    /**
     * @deprecated Requesting the node path is just requesting one particular index field, and
     * {@link Builder#returnFields(IndexPath...)} with {@link NodeIndexPath#PATH} does exactly that - {@link NodeHit#getNodePath()} is
     * populated whenever the path is among the requested fields. Scheduled for removal.
     */
    @Deprecated
    public boolean isWithPath()
    {
        return withPath;
    }

    /**
     * Index fields to fetch for every hit, available per hit through {@link NodeHit#getFields()}.
     *
     * @since 8.1.0
     */
    public Set<IndexPath> getReturnFields()
    {
        return returnFields;
    }

    public static Builder create()
    {
        return new Builder();
    }

    /**
     * A builder preloaded with every setting of an already built query.
     *
     * @since 8.1.0
     */
    public static Builder create( final NodeQuery source )
    {
        return new Builder( source );
    }

    public static final class Builder
        extends AbstractQuery.Builder<Builder>
    {
        private NodePath parent;

        private boolean recursive = false;

        private boolean withPath = false;

        private final Set<IndexPath> returnFields = new LinkedHashSet<>();

        private Builder()
        {
            super();
        }

        private Builder( final NodeQuery source )
        {
            super( source );
            this.parent = source.parent;
            this.recursive = source.recursive;
            this.withPath = source.withPath;
            this.returnFields.addAll( source.returnFields );
        }

        /**
         * Restricts the query to the direct children of the node at the given path, or with {@link #recursive(boolean)} to every
         * descendant. Combines with every other constraint of the query. When the query specifies no order expressions, results come back
         * in the child order of the parent.
         */
        public Builder parent( final NodePath parent )
        {
            this.parent = parent;
            return this;
        }

        /**
         * Widens the {@link #parent(NodePath)} restriction from the direct children to every descendant, at any depth. Expects a parent.
         * <p>
         * Note that the child order of a parent orders its own children, so it rarely says anything meaningful about a whole subtree:
         * specify order expressions when ordering a recursive result matters.
         *
         * @since 8.1.0
         */
        public Builder recursive( final boolean recursive )
        {
            this.recursive = recursive;
            return this;
        }

        /**
         * @deprecated Use {@link #returnFields(IndexPath...)} with {@link NodeIndexPath#PATH} instead. Scheduled for removal.
         */
        @Deprecated
        public Builder withPath( final boolean withPath )
        {
            this.withPath = withPath;
            return this;
        }

        /**
         * Adds index fields to fetch for every hit. The values arrive per hit in {@link NodeHit#getFields()}, exactly as the search index
         * stores them: every field as a list, values in their string form unless a typed variant like {@code ._number} (double) or
         * {@code ._datetime} is requested. A field the index does not hold for a hit is absent rather than empty; a field indexed with
         * none of its variants matching cannot come back at all.
         *
         * @since 8.1.0
         */
        public Builder returnFields( final IndexPath... fields )
        {
            for ( final IndexPath field : fields )
            {
                this.returnFields.add( field );
            }
            return this;
        }

        public NodeQuery build()
        {
            return new NodeQuery( this );
        }
    }

}
