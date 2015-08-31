package com.enonic.wem.repo.internal.index.result;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.enonic.wem.repo.internal.index.IndexFieldNameNormalizer;

public class SearchHit
{
    private final float score;

    private final String id;

    private final long version;//  = -1;

    private final Map<String, ReturnValue> fields;

    private SearchHit( final Builder builder )
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

    public ReturnValue getField( final String fieldName )
    {
        return doGetField( fieldName, false );
    }

    public ReturnValue getField( final String fieldName, final boolean failOnMissing )
    {
        return doGetField( fieldName, failOnMissing );
    }

    public String getStringValue( final String fieldName )
    {
        final ReturnValue returnValue = doGetField( fieldName, true );

        if ( returnValue.getSingleValue() == null )
        {
            return null;
        }
        else
        {
            return returnValue.getSingleValue().toString();
        }
    }

    private ReturnValue doGetField( final String fieldName, final boolean failOnMissing )
    {
        final String normalizedFieldName = IndexFieldNameNormalizer.normalize( fieldName );

        final ReturnValue returnValue = fields.get( normalizedFieldName );

        if ( failOnMissing && returnValue == null )
        {
            throw new RuntimeException( "Expected field " + normalizedFieldName + " in result not found" );
        }

        return returnValue;
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

        private Map<String, ReturnValue> fields = Maps.newHashMap();

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

        public Builder setFields( final Map<String, ReturnValue> fields )
        {
            this.fields = fields;
            return this;
        }

        public Builder addField( final String fieldName, final ReturnValue returnValue )
        {
            this.fields.put( fieldName, returnValue );
            return this;
        }

        public SearchHit build()
        {
            return new SearchHit( this );
        }
    }
}
