package com.enonic.wem.api.content.page.layout;


import com.enonic.wem.api.content.page.AbstractRegions;
import com.enonic.wem.api.content.page.ComponentPath;
import com.enonic.wem.api.content.page.region.Region;

public final class LayoutRegions
    extends AbstractRegions
{
    private LayoutRegions( final Builder builder )
    {
        super( builder );
    }

    public void applyComponentPaths( final ComponentPath parent )
    {
        for ( final Region region : this )
        {
            region.applyComponentPaths( parent );
        }
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


