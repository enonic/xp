package com.enonic.xp.site.filter;

public class FilterDescriptor
{

    private final FilterType type;

    private final String name;

    private final int order;

    private FilterDescriptor( final Builder builder )
    {
        this.type = builder.type;
        this.name = builder.name;
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

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private FilterType type = FilterType.RESPONSE;

        private String name;

        private int order;

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

        public FilterDescriptor build()
        {
            return new FilterDescriptor( this );
        }
    }
}
