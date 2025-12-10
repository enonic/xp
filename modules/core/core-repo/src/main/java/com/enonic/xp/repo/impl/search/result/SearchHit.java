package com.enonic.xp.repo.impl.search.result;

import com.enonic.xp.highlight.HighlightedProperties;
import com.enonic.xp.query.QueryExplanation;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.sortvalues.SortValuesProperty;

public class SearchHit
{
    private final float score;

    private final String id;

    private final String indexName;

    private final String indexType;

    private final ReturnValues returnValues;

    private final QueryExplanation explanation;

    private final HighlightedProperties highlightedProperties;

    private final SortValuesProperty sortValues;

    private SearchHit( final Builder builder )
    {
        this.score = builder.score;
        this.id = builder.id;
        this.returnValues = builder.returnValues;
        this.indexName = builder.indexName;
        this.indexType = builder.indexType;
        this.explanation = builder.explanation;
        this.highlightedProperties = builder.highlightedProperties;
        this.sortValues = builder.sortValues;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public float getScore()
    {
        return score;
    }

    public String getId()
    {
        return id;
    }

    public String getIndexName()
    {
        return indexName;
    }

    public String getIndexType()
    {
        return indexType;
    }

    public QueryExplanation getExplanation()
    {
        return explanation;
    }

    public ReturnValues getReturnValues()
    {
        return returnValues;
    }

    public HighlightedProperties getHighlightedProperties()
    {
        return highlightedProperties;
    }

    public SortValuesProperty getSortValues()
    {
        return sortValues;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof SearchHit ) )
        {
            return false;
        }

        final SearchHit that = (SearchHit) o;

        return id != null ? id.equals( that.id ) : that.id == null;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }

    public static class Builder
    {
        private float score = 0;

        private String id;

        private String indexName;

        private String indexType;

        private ReturnValues returnValues;

        private QueryExplanation explanation;

        private HighlightedProperties highlightedProperties;

        private SortValuesProperty sortValues;

        public Builder score( final float score )
        {
            this.score = score;
            return this;
        }

        public Builder id( final String id )
        {
            this.id = id;
            return this;
        }

        public Builder returnValues( final ReturnValues returnValues )
        {
            this.returnValues = returnValues;
            return this;
        }

        public Builder indexName( final String indexName )
        {
            this.indexName = indexName;
            return this;
        }

        public Builder indexType( final String indexType )
        {
            this.indexType = indexType;
            return this;
        }

        public Builder explanation( final QueryExplanation explanation )
        {
            this.explanation = explanation;
            return this;
        }

        public Builder highlightedFields( final HighlightedProperties highlightedProperties )
        {
            this.highlightedProperties = highlightedProperties;
            return this;
        }

        public Builder sortValues( final SortValuesProperty sortValues )
        {
            this.sortValues = sortValues;
            return this;
        }

        public SearchHit build()
        {
            return new SearchHit( this );
        }
    }
}
