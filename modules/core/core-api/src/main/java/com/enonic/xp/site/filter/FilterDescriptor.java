package com.enonic.xp.site.filter;

import com.enonic.xp.app.ApplicationKey;

public final class FilterDescriptor
{

    private final FilterType type;

    private final String name;

    private final int order;

    private final ApplicationKey application;

    private FilterDescriptor( final Builder builder )
    {
        this.type = builder.type;
        this.name = builder.name;
        this.order = builder.order;
        this.application = builder.application;
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

        public void application( final ApplicationKey application )
        {
            this.application = application;
        }

        public FilterDescriptor build()
        {
            return new FilterDescriptor( this );
        }
    }
}
