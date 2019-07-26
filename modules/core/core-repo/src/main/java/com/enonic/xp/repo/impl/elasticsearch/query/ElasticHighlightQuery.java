package com.enonic.xp.repo.impl.elasticsearch.query;

import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.search.highlight.HighlightBuilder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class ElasticHighlightQuery
{
    private final ImmutableSet<HighlightBuilder.Field> fields;

    private ElasticHighlightQuery( final Builder builder )
    {
        this.fields = ImmutableSet.copyOf( builder.fields );
    }

    public static ElasticHighlightQuery empty()
    {
        return new Builder().build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ImmutableSet<HighlightBuilder.Field> getFields()
    {
        return fields;
    }

    @Override
    public String toString()
    {
        return "ElasticHighlightQuery{" + "fields=[ " + StringUtils.join( fields.toArray(), "," ) + " ]}";
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
        final ElasticHighlightQuery that = (ElasticHighlightQuery) o;
        return Objects.equals( fields, that.fields );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( fields );
    }

    public static class Builder
    {

        private Set<HighlightBuilder.Field> fields = Sets.newHashSet();


        public Builder addField( final HighlightBuilder.Field value )
        {
            this.fields.add( value );
            return this;
        }

        public ElasticHighlightQuery build()
        {
            return new ElasticHighlightQuery( this );
        }

    }


}
