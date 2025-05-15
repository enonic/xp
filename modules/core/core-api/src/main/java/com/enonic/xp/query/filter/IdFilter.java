package com.enonic.xp.query.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.MoreObjects;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.UUID;

public final class IdFilter
    extends FieldFilter
{
    private final List<String> values;

    private IdFilter( final Builder builder )
    {
        super( builder );
        values = builder.values;
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
        private final List<String> values = new ArrayList<>();

        private Builder()
        {
            this.fieldName( NodeIndexPath.ID.getPath() );
        }

        public Builder values( final NodeIds val )
        {
            for ( NodeId value : val )
            {
                this.values.add( value.toString() );
            }
            return this;
        }

        public Builder values( final Iterable<? extends UUID> values )
        {
            for ( UUID value : values )
            {
                this.values.add( value.toString() );
            }
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
