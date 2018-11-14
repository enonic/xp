package com.enonic.xp.site.processor;

import java.util.Objects;

import com.enonic.xp.app.ApplicationKey;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ResponseProcessorDescriptor
{
    private final String name;

    private final int order;

    private final ApplicationKey application;

    private ResponseProcessorDescriptor( final Builder builder )
    {
        this.name = checkNotNull( builder.name, "name cannot be null" );
        this.application = checkNotNull( builder.application, "application cannot be null" );
        this.order = builder.order;
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
        final ResponseProcessorDescriptor that = (ResponseProcessorDescriptor) o;
        return Objects.equals( order, that.order ) && Objects.equals( name, that.name ) && Objects.equals( application, that.application );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, order, application );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String name;

        private int order;

        private ApplicationKey application;

        private Builder()
        {
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

        public ResponseProcessorDescriptor build()
        {
            return new ResponseProcessorDescriptor( this );
        }
    }
}
