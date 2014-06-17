package com.enonic.wem.api.content.page;


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

    public static class Builder
        extends AbstractRegions.Builder<Builder>
    {
        public PageRegions build()
        {
            return new PageRegions( this );
        }
    }
}


