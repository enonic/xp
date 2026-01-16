package com.enonic.xp.node;

import java.util.Objects;
import java.util.Set;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

/**
 * Represents the parameters used to apply attributes to a specific node version.
 * This class encapsulates details about the node version and the attributes
 * to be added or removed.
 * <p>
 * See {@link NodeService#applyVersionAttributes(ApplyVersionAttributesParams)} for more details.
 */
@NullMarked
public final class ApplyVersionAttributesParams
{
    private final NodeVersionId nodeVersionId;

    private final Attributes addAttributes;

    private final Set<String> removeAttributes;

    private ApplyVersionAttributesParams( final Builder builder )
    {
        this.nodeVersionId = Objects.requireNonNull( builder.nodeVersionId );
        this.addAttributes = builder.addAttributes.build();
        this.removeAttributes = builder.removeAttributes.build();
    }

    public NodeVersionId getNodeVersionId()
    {
        return nodeVersionId;
    }

    public Attributes getAddAttributes()
    {
        return addAttributes;
    }

    public Set<String> getRemoveAttributes()
    {
        return removeAttributes;
    }

    /**
     * Creates a new {@link Builder} instance for constructing {@link ApplyVersionAttributesParams}.
     *
     * @return a new {@link Builder} instance
     */
    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        @Nullable
        private NodeVersionId nodeVersionId;

        private final Attributes.Builder addAttributes = Attributes.create();

        private final ImmutableSet.Builder<String> removeAttributes = ImmutableSet.builder();

        private Builder()
        {
        }

        /**
         * Sets the NodeVersionId of the node version to which the attributes will be applied.
         *
         * @param val the NodeVersionId to which the attributes will be applied.
         * @return the updated Builder instance.
         */
        public Builder nodeVersionId( final NodeVersionId val )
        {
            this.nodeVersionId = val;
            return this;
        }

        /**
         * Adds all entries from the specified {@link Attributes} instance to this builder.
         *
         * @param val the Attributes instance containing entries to be added
         * @return the updated Builder instance
         */
        public Builder addAttributes( final Attributes val )
        {
            this.addAttributes.addAll( val.entrySet() );
            return this;
        }

        /**
         * Adds a collection of attribute keys to the list of attributes to be removed
         * during the update process.
         *
         * @param val an Iterable collection of attribute names to be removed
         * @return the updated Builder instance
         */
        public Builder removeAttributes( final Iterable<String> val )
        {
            this.removeAttributes.addAll( val );
            return this;
        }

        /**
         * Builds and returns a new instance of {@link ApplyVersionAttributesParams}
         * with the configuration defined in this {@link Builder}.
         *
         * @return a new {@link ApplyVersionAttributesParams} instance
         */
        public ApplyVersionAttributesParams build()
        {
            return new ApplyVersionAttributesParams( this );
        }
    }
}
