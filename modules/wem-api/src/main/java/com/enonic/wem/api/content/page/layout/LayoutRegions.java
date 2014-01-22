package com.enonic.wem.api.content.page.layout;


import com.enonic.wem.api.content.page.AbstractRegions;

public final class LayoutRegions
    extends AbstractRegions
{
    private LayoutRegions( final Builder builder )
    {
        super( builder );
    }

    public static Builder newLayoutRegions()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractRegions.Builder<Builder>
    {
        public LayoutRegions build()
        {
            return new LayoutRegions( this );
        }
    }
}


