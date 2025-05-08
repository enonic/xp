package com.enonic.xp.repo.impl.elasticsearch.query;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.elasticsearch.search.highlight.HighlightBuilder;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.query.highlight.HighlightQuerySettings;
import com.enonic.xp.query.highlight.constants.Encoder;
import com.enonic.xp.query.highlight.constants.Fragmenter;
import com.enonic.xp.query.highlight.constants.Order;
import com.enonic.xp.query.highlight.constants.TagsSchema;

public class ElasticHighlightQuery
{
    private final ImmutableSet<HighlightBuilder.Field> fields;

    private final HighlightQuerySettings settings;

    private ElasticHighlightQuery( final Builder builder )
    {
        this.fields = ImmutableSet.copyOf( builder.fields );
        this.settings = builder.settings;
    }

    public ImmutableSet<HighlightBuilder.Field> getFields()
    {
        return fields;
    }

    public Encoder getEncoder()
    {
        return doGetSettings().getEncoder();
    }

    public Fragmenter getFragmenter()
    {
        return doGetSettings().getFragmenter();
    }

    public Integer getFragmentSize()
    {
        return doGetSettings().getFragmentSize();
    }

    public Integer getNoMatchSize()
    {
        return doGetSettings().getNoMatchSize();
    }

    public Integer getNumOfFragments()
    {
        return doGetSettings().getNumOfFragments();
    }

    public Order getOrder()
    {
        return doGetSettings().getOrder();
    }

    public List<String> getPreTags()
    {
        return doGetSettings().getPreTags();
    }

    public List<String> getPostTags()
    {
        return doGetSettings().getPostTags();
    }

    public Boolean getRequireFieldMatch()
    {
        return doGetSettings().getRequireFieldMatch();
    }

    public TagsSchema getTagsSchema()
    {
        return doGetSettings().getTagsSchema();
    }

    private HighlightQuerySettings doGetSettings()
    {
        return settings;
    }

    public static ElasticHighlightQuery empty()
    {
        return new Builder().build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public String toString()
    {
        return "ElasticHighlightQuery{" + "fields=[ " + fields.stream().map( Objects::toString ).
            collect( Collectors.joining( "," ) ) + " ]}";
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
        return Objects.equals( fields, that.fields ) && Objects.equals( settings, that.settings );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( fields, settings );
    }

    public static class Builder
    {
        private final Set<HighlightBuilder.Field> fields = new HashSet<>();

        private HighlightQuerySettings settings = HighlightQuerySettings.empty();


        public Builder addField( final HighlightBuilder.Field value )
        {
            this.fields.add( value );
            return this;
        }

        public Builder settings( final HighlightQuerySettings settings )
        {
            this.settings = settings;
            return this;
        }

        public ElasticHighlightQuery build()
        {
            return new ElasticHighlightQuery( this );
        }

    }

}
