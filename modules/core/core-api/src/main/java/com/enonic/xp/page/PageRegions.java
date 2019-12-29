package com.enonic.xp.page;


import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.region.AbstractRegions;

@PublicApi
public final class PageRegions
    extends AbstractRegions
{
    private PageRegions( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final PageRegions source )
    {
        return new Builder( source );
    }

    @Override
    public PageRegions copy()
    {
        return PageRegions.create( this ).build();
    }

    public static class Builder
        extends AbstractRegions.Builder<Builder>
    {
        private Builder( final PageRegions source )
        {
            super( source );
        }

        private Builder()
        {
            // Default
        }

        public PageRegions build()
        {
            return new PageRegions( this );
        }
    }
}


