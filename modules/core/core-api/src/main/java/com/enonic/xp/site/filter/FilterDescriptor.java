package com.enonic.xp.site.filter;

import java.util.Objects;

import com.enonic.xp.app.ApplicationKey;

import static com.google.common.base.Preconditions.checkNotNull;

public final class FilterDescriptor
{

    private final FilterType type;

    private final String name;

    private final int order;

    private final ApplicationKey application;

    private FilterDescriptor( final Builder builder )
    {
        this.type = checkNotNull( builder.type, "type cannot be null" );
        this.name = checkNotNull( builder.name, "name cannot be null" );
        this.application = checkNotNull( builder.application, "application cannot be null" );
        this.order = builder.order;
    }

    public FilterType getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public int getOrder()
    {
        return order;
    }

    public ApplicationKey getApplication()
    {
        return application;
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
        final FilterDescriptor that = (FilterDescriptor) o;
        return Objects.equals( order, that.order ) &&
            Objects.equals( type, that.type ) &&
            Objects.equals( name, that.name ) &&
            Objects.equals( application, that.application );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( type, name, order, application );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private FilterType type = FilterType.RESPONSE;

        private String name;

        private int order;

        private ApplicationKey application;

        private Builder()
        {
        }

        public Builder type( final FilterType type )
        {
            this.type = type;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder order( final int order )
        {
            this.order = order;
            return this;
        }

        public Builder application( final ApplicationKey application )
        {
            this.application = application;
            return this;
        }

        public FilterDescriptor build()
        {
            return new FilterDescriptor( this );
        }
    }
}
