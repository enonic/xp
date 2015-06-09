package com.enonic.xp.page;


import com.google.common.annotations.Beta;

import com.enonic.xp.region.AbstractRegions;

@Beta
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


