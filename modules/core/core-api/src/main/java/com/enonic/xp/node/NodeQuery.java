package com.enonic.xp.node;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.index.IndexPath;

public final class NodeQuery
    extends AbstractQuery
{
    public static final int ALL_RESULTS_SIZE_FLAG = -1;

    /**
     * The fields that may be requested per hit. The set is limited to fields a {@link Node} exposes, so requesting them is a less
     * expensive way of reading values the node itself would supply, and grants access to nothing further.
     *
     * @since 8.1.0
     */
    public static final Set<IndexPath> SUPPORTED_RETURN_FIELDS =
        Set.of( NodeIndexPath.NAME, NodeIndexPath.PATH, NodeIndexPath.NODE_TYPE, NodeIndexPath.VERSION, NodeIndexPath.TIMESTAMP );

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
         * Restricts the query to the direct children of the node at the given path, or to every descendant when
         * {@link #recursive(boolean)} is set. The restriction combines with all other constraints of the query. Where the query specifies
         * no order expressions, the results are returned in the child order of the parent.
         */
        public Builder parent( final NodePath parent )
        {
            this.parent = parent;
            return this;
        }

        /**
         * Widens the {@link #parent(NodePath)} restriction from the direct children to every descendant, at any depth. A parent is
         * required.
         * <p>
         * The child order of the parent is not applied to a subtree, since it orders siblings and would otherwise sort levels against
         * each other by a value that is comparable between siblings only. Specify order expressions where the order of a recursive
         * result is significant.
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
         * Requests fields to be returned with every hit, in {@link NodeHit#getFields()}. Only fields in
         * {@link #SUPPORTED_RETURN_FIELDS} may be requested. Each field is returned as a list of strings, {@code _ts} in ISO-8601 form,
         * and a field for which a hit holds no value is absent rather than empty.
         *
         * @throws IllegalArgumentException for a field outside {@link #SUPPORTED_RETURN_FIELDS}.
         * @since 8.1.0
         */
        public Builder returnFields( final IndexPath... fields )
        {
            for ( final IndexPath field : fields )
            {
                Preconditions.checkArgument( SUPPORTED_RETURN_FIELDS.contains( field ), "unsupported return field: %s", field );
                this.returnFields.add( field );
            }
            return this;
        }

        /**
         * Requests fields that an API layered on nodes has already validated against its own supported set, such as the content API
         * validating content field names. Callers naming node fields should use {@link #returnFields(IndexPath...)}, which validates
         * them.
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
