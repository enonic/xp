package com.enonic.xp.repo.impl.index.result;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.xp.repo.impl.index.IndexFieldNameNormalizer;

public class SearchResultEntry
{
    private final float score;

    private final String id;

    private final long version;//  = -1;

    private final Map<String, SearchResultFieldValue> fields;


    private SearchResultEntry( final Builder builder )
    {
        this.score = builder.score;
        this.id = builder.id;
        this.version = builder.version;
        this.fields = ImmutableMap.copyOf( builder.fields );
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

    public long getVersion()
    {
        return version;
    }

    public Map<String, SearchResultFieldValue> getFields()
    {
        return fields;
    }

    public SearchResultFieldValue getField( final String fieldName )
    {
        return doGetField( fieldName, false );
    }

    public SearchResultFieldValue getField( final String fieldName, final boolean failOnMissing )
    {
        return doGetField( fieldName, failOnMissing );
    }

    public String getStringValue( final String fieldName )
    {
        final SearchResultFieldValue searchResultFieldValue = doGetField( fieldName, true );

        if ( searchResultFieldValue.getValue() == null )
        {
            return null;
        }
        else
        {
            return searchResultFieldValue.getValue().toString();
        }
    }

    private SearchResultFieldValue doGetField( final String fieldName, final boolean failOnMissing )
    {
        final String normalizedFieldName = IndexFieldNameNormalizer.normalize( fieldName );

        final SearchResultFieldValue searchResultFieldValue = fields.get( normalizedFieldName );

        if ( failOnMissing && searchResultFieldValue == null )
        {
            throw new RuntimeException( "Expected field " + normalizedFieldName + " in result not found" );
        }

        return searchResultFieldValue;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof SearchResultEntry ) )
        {
            return false;
        }

        final SearchResultEntry that = (SearchResultEntry) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }

    public static class Builder
    {
        private float score = Float.NEGATIVE_INFINITY;

        private String id;

        private long version = -1;

        private Map<String, SearchResultFieldValue> fields = Maps.newHashMap();

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

        public Builder version( final long version )
        {
            this.version = version;
            return this;
        }

        public Builder setFields( final Map<String, SearchResultFieldValue> fields )
        {
            this.fields = fields;
            return this;
        }

        public Builder addField( final String fieldName, final SearchResultFieldValue searchResultFieldValue )
        {
            this.fields.put( fieldName, searchResultFieldValue );
            return this;
        }

        public SearchResultEntry build()
        {
            return new SearchResultEntry( this );
        }
    }
}
