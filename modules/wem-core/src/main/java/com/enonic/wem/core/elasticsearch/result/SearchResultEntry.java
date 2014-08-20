package com.enonic.wem.core.elasticsearch.result;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class SearchResultEntry
{
    private final float score;

    private final String id;

    private final long version;//  = -1;

    private final Map<String, SearchResultField> fields;


    public SearchResultEntry( final Builder builder )
    {
        this.score = builder.score;
        this.id = builder.id;
        this.version = builder.version;
        this.fields = ImmutableMap.copyOf( builder.fields );
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

    public Map<String, SearchResultField> getFields()
    {
        return fields;
    }

    public SearchResultField getField( final String fieldName )
    {
        return doGetField( fieldName, false );
    }

    public SearchResultField getField( final String fieldName, final boolean failOnMissing )
    {
        return doGetField( fieldName, failOnMissing );
    }

    private SearchResultField doGetField( final String fieldName, final boolean failOnMissing )
    {
        final SearchResultField searchResultField = fields.get( fieldName );

        if ( failOnMissing && searchResultField == null )
        {
            throw new RuntimeException( "Expected field " + fieldName + " in result not found" );
        }

        return searchResultField;
    }


    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private float score = Float.NEGATIVE_INFINITY;

        private String id;

        private long version = -1;

        private Map<String, SearchResultField> fields = Maps.newHashMap();

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

        public Builder setFields( final Map<String, SearchResultField> fields )
        {
            this.fields = fields;
            return this;
        }

        public Builder addField( final String fieldName, final SearchResultField searchResultField )
        {
            this.fields.put( fieldName, searchResultField );
            return this;
        }

        public SearchResultEntry build()
        {
            return new SearchResultEntry( this );
        }
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
}
