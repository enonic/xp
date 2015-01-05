package com.enonic.wem.api.content.page;


import com.enonic.wem.api.content.page.region.AbstractRegions;

public final class PageRegions
    extends AbstractRegions
{
    private PageRegions( final Builder builder )
    {
        super( builder );
    }

    public static Builder newPageRegions()
    {
        return new Builder();
    }

    public static Builder newPageRegions( final PageRegions source )
    {
        return new Builder( source );
    }

    public PageRegions copy()
    {
        return PageRegions.newPageRegions( this ).build();
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


