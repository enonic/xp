package com.enonic.xp.node;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.index.IndexPath;

public final class NodeQuery
    extends AbstractQuery
{
    public static final int ALL_RESULTS_SIZE_FLAG = -1;

    private final NodePath parent;

    private final boolean withPath;

    private final ImmutableSet<IndexPath> returnFields;

    private NodeQuery( final Builder builder )
    {
        super( builder );
        this.parent = builder.parent;
        this.withPath = builder.withPath;
        this.returnFields = ImmutableSet.copyOf( builder.returnFields );
    }

    public NodePath getParent()
    {
        return parent;
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
            this.withPath = source.withPath;
            this.returnFields.addAll( source.returnFields );
        }

        public Builder parent( final NodePath parent )
        {
            this.parent = parent;
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
