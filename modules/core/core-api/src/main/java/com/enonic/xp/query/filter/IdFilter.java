package com.enonic.xp.query.filter;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.node.NodeIds;

public class IdFilter
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
            values.addAll( val.getAsStrings() );
            return this;
        }

        public Builder values( final Collection<String> values )
        {
            values.addAll( values );
            return this;
        }

        public IdFilter build()
        {
            return new IdFilter( this );
        }
    }
}
