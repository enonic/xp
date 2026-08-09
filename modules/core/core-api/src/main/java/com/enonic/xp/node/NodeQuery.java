package com.enonic.xp.node;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.index.IndexPath;

public final class NodeQuery
    extends AbstractQuery
{
    public static final int ALL_RESULTS_SIZE_FLAG = -1;

    /**
     * The fields a query may ask back per hit, mapped to the name a {@link Node} shows each of them by. Only fields a node shows are
     * available, so asking for them is a cheaper way to read what a node would tell anyway, never a way to reach anything else.
     * <p>
     * Keys are index paths, and therefore lowercase; values are the names callers see on a hit.
     *
     * @since 8.1.0
     */
    public static final Map<IndexPath, String> SUPPORTED_RETURN_FIELDS =
        Map.of( NodeIndexPath.NAME, "_name", NodeIndexPath.PATH, "_path", NodeIndexPath.NODE_TYPE, "_nodeType", NodeIndexPath.VERSION,
                "_versionKey", NodeIndexPath.TIMESTAMP, "_ts" );

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
     * @deprecated Ask for {@link NodeIndexPath#PATH} through {@link Builder#returnFields(IndexPath...)} instead;
     * {@link NodeHit#getNodePath()} answers whenever the path was asked for. Scheduled for removal.
     */
    @Deprecated
    public boolean isWithPath()
    {
        return withPath;
    }

    /**
     * The fields asked to come back with every hit, delivered through {@link NodeHit#getFields()}.
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

        private boolean recursive;

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
         * Asks for fields to come back with every hit, from {@link #SUPPORTED_RETURN_FIELDS} only, in {@link NodeHit#getFields()}.
         * Each field arrives as a list of strings - {@code _ts} in ISO-8601 form - and a field a hit has no value for is absent rather
         * than empty.
         *
         * @throws IllegalArgumentException for a field outside {@link #SUPPORTED_RETURN_FIELDS}.
         * @since 8.1.0
         */
        public Builder returnFields( final IndexPath... fields )
        {
            for ( final IndexPath field : fields )
            {
                Preconditions.checkArgument( SUPPORTED_RETURN_FIELDS.containsKey( field ), "unsupported return field: %s", field );
                this.returnFields.add( field );
            }
            return this;
        }

        /**
         * Asks for fields an API built on top of nodes has already checked against its own supported set, such as the content API
         * answering for content field names. Callers stating node fields want {@link #returnFields(IndexPath...)}, which checks them.
         *
         * @since 8.1.0
         */
        public Builder checkedReturnFields( final Collection<IndexPath> fields )
        {
            this.returnFields.addAll( fields );
            return this;
        }

        public NodeQuery build()
        {
            return new NodeQuery( this );
        }
    }

}
