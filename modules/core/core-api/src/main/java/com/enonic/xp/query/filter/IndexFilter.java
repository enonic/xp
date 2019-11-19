package com.enonic.xp.query.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.MoreObjects;

public class IndexFilter
    extends FieldFilter
{

    private final List<String> values;

    private IndexFilter( final Builder builder )
    {
        super( builder );
        this.values = builder.values;
    }

    @Override
    public String getFieldName()
    {
        return "_index";
    }

    public List<String> getValues()
    {
        return values;
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper( this ).
            omitNullValues().
            add( "fieldName", getFieldName() ).
            add( "values", values ).
            toString();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends FieldFilter.Builder<Builder>
    {

        private List<String> values = new ArrayList<>();

        private Builder()
        {
        }

        public Builder values( final Collection<String> values )
        {
            this.values.addAll( values );
            return this;
        }

        public Builder value( final String value )
        {
            this.values.add( value );
            return this;
        }

        public IndexFilter build()
        {
            return new IndexFilter( this );
        }

    }

}
