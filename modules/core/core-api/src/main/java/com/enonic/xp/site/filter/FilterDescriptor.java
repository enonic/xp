package com.enonic.xp.site.filter;

public class FilterDescriptor
{

    private final FilterType filterType;

    private final String name;

    private final int order;

    private FilterDescriptor( final Builder builder )
    {
        this.filterType = builder.filterType;
        this.name = builder.name;
        this.order = builder.order;
    }

    public FilterType getFilterType()
    {
        return filterType;
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
        private FilterType filterType = FilterType.RESPONSE;

        private String name;

        private int order;

        @SuppressWarnings("unused")
        public Builder filterType( final FilterType filterType )
        {
            this.filterType = filterType;
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
