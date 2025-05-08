package com.enonic.xp.query.highlight;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.query.highlight.constants.Fragmenter;
import com.enonic.xp.query.highlight.constants.Order;

public class HighlightPropertySettings
{
    private final Fragmenter fragmenter;

    private final Integer fragmentSize;

    private final Integer noMatchSize;

    private final Integer numOfFragments;

    private final Order order;

    private final ImmutableList<String> preTags;

    private final ImmutableList<String> postTags;

    private final Boolean requireFieldMatch;

    protected HighlightPropertySettings( final Builder builder )
    {
        this.fragmenter = builder.fragmenter;
        this.fragmentSize = builder.fragmentSize;
        this.noMatchSize = builder.noMatchSize;
        this.numOfFragments = builder.numOfFragments;
        this.order = builder.order;
        this.preTags = builder.preTags.build();
        this.postTags = builder.postTags.build();
        this.requireFieldMatch = builder.requireFieldMatch;
    }

    public Fragmenter getFragmenter()
    {
        return fragmenter;
    }

    public Integer getFragmentSize()
    {
        return fragmentSize;
    }

    public Integer getNoMatchSize()
    {
        return noMatchSize;
    }

    public Integer getNumOfFragments()
    {
        return numOfFragments;
    }

    public Order getOrder()
    {
        return order;
    }

    public List<String> getPreTags()
    {
        return preTags;
    }

    public List<String> getPostTags()
    {
        return postTags;
    }

    public Boolean getRequireFieldMatch()
    {
        return requireFieldMatch;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final HighlightPropertySettings that = (HighlightPropertySettings) o;
        return fragmenter == that.fragmenter && Objects.equals( fragmentSize, that.fragmentSize ) &&
            Objects.equals( noMatchSize, that.noMatchSize ) && Objects.equals( numOfFragments, that.numOfFragments ) &&
            order == that.order && Objects.equals( preTags, that.preTags ) && Objects.equals( postTags, that.postTags ) &&
            Objects.equals( requireFieldMatch, that.requireFieldMatch );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( fragmenter, fragmentSize, noMatchSize, numOfFragments, order, preTags, postTags, requireFieldMatch );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static HighlightPropertySettings empty()
    {
        return create().build();
    }


    public static class Builder<T extends Builder>
    {
        private Fragmenter fragmenter;

        private Integer fragmentSize;

        private Integer noMatchSize;

        private Integer numOfFragments;

        private Order order;

        private final ImmutableList.Builder<Object> preTags = ImmutableList.builder();

        private final ImmutableList.Builder<Object> postTags = ImmutableList.builder();

        private Boolean requireFieldMatch = false;

        public T fragmenter( final Fragmenter fragmenter )
        {
            this.fragmenter = fragmenter;
            return (T)this;
        }

        public T fragmentSize( final Integer fragmentSize )
        {
            this.fragmentSize = fragmentSize;
            return (T)this;
        }

        public T noMatchSize( final Integer noMatchSize )
        {
            this.noMatchSize = noMatchSize;
            return (T)this;
        }

        public T numOfFragments( final Integer numOfFragments )
        {
            this.numOfFragments = numOfFragments;
            return (T)this;
        }

        public T order( final Order order )
        {
            this.order = order;
            return (T)this;
        }

        public T addPreTags( final List<String> preTags )
        {
            if(preTags != null)
            {
                this.preTags.addAll( preTags );
            }
            return (T)this;
        }

        public T addPostTags( final List<String> postTags )
        {
            if(postTags != null)
            {
                this.postTags.addAll( postTags );
            }
            return (T)this;
        }

        public T requireFieldMatch( final Boolean requireFieldMatch )
        {
            this.requireFieldMatch = requireFieldMatch;
            return (T)this;
        }

        public HighlightPropertySettings build()
        {
            return new HighlightPropertySettings( this );
        }
    }
}
