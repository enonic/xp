package com.enonic.xp.schema.mixin;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

public class XData
    extends Mixin
{
    private final ImmutableList<String> allowContentTypes;

    private XData( final Builder builder )
    {
        super( builder );
        this.allowContentTypes = builder.allowContentTypes.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final XData xData )
    {
        return new Builder( xData );
    }

    public List<String> getAllowContentTypes()
    {
        return allowContentTypes;
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
        if ( !super.equals( o ) )
        {
            return false;
        }
        final XData xData = (XData) o;
        return Objects.equals( allowContentTypes, xData.allowContentTypes );
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( super.hashCode(), allowContentTypes );
    }

    public static class Builder
        extends Mixin.Builder<Builder>
    {
        private ImmutableList.Builder<String> allowContentTypes = ImmutableList.builder();

        public Builder()
        {
            super();
        }

        public Builder( final XData xData )
        {
            super( xData );
            this.allowContentTypes = ImmutableList.<String>builder().addAll( xData.getAllowContentTypes() );
        }

        public Builder allowContentType( final String value )
        {
            this.allowContentTypes.add( value );
            return this;
        }

        public Builder allowContentTypes( final Collection<String> value )
        {
            this.allowContentTypes.addAll( value );
            return this;
        }

        public XData build()
        {
            return new XData( this );
        }
    }
}
