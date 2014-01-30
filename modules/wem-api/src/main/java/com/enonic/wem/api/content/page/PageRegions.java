package com.enonic.wem.api.content.page;


import com.enonic.wem.api.content.page.region.Region;

public final class PageRegions
    extends AbstractRegions
{
    private PageRegions( final Builder builder )
    {
        super( builder );
        applyComponentPaths();
    }

    private void applyComponentPaths()
    {
        for ( final Region region : this )
        {
            region.applyComponentPaths( null );
        }
    }

    public static Builder newPageRegions()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractRegions.Builder<Builder>
    {
        public PageRegions build()
        {
            return new PageRegions( this );
        }
    }
}


