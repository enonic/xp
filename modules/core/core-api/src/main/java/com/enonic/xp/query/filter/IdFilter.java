package com.enonic.xp.query.filter;

import java.util.Collection;
import java.util.List;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;

public class IdFilter
    extends FieldFilter
{
    private final List<String> values;

    private final String defaultIdFieldName = NodeIndexPath.ID.getPath();

    private IdFilter( final Builder builder )
    {
        super( builder );
        values = builder.values;
    }

    @Override
    public String getFieldName()
    {
        if ( Strings.isNullOrEmpty( this.fieldName ) )
        {
            return defaultIdFieldName;
        }

        return this.fieldName;
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
            add( "fieldName", fieldName ).
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
        private List<String> values = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder values( final NodeIds val )
        {
            this.values.addAll( val.getAsStrings() );
            return this;
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

        public IdFilter build()
        {
            return new IdFilter( this );
        }
    }
}
