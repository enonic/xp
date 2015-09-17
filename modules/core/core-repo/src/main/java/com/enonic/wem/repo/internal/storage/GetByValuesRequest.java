package com.enonic.wem.repo.internal.storage;

import java.util.Map;

import com.google.common.collect.Maps;

public class GetByValuesRequest
    extends AbstractGetRequest
{
    private final Map<String, Object> values;

    private final boolean expectSingleValue;

    private GetByValuesRequest( final Builder builder )
    {
        super( builder );
        this.values = builder.values;
        this.expectSingleValue = builder.expectSingleValue;
    }

    public boolean expectSingleValue()
    {
        return expectSingleValue;
    }

    public Map<String, Object> getValues()
    {
        return values;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractGetRequest.Builder<Builder>
    {
        private Map<String, Object> values = Maps.newHashMap();

        private boolean expectSingleValue;

        public Builder addValue( final String fieldName, final Object value )
        {
            this.values.put( fieldName, value );
            return this;
        }

        public Builder expectSingleValue( final boolean expectSingleValue )
        {
            this.expectSingleValue = expectSingleValue;
            return this;
        }

        public GetByValuesRequest build()
        {
            return new GetByValuesRequest( this );
        }

    }


}
