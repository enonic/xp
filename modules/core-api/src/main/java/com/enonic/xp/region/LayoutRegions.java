package com.enonic.xp.region;

import com.google.common.annotations.Beta;

@Beta
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

    public static Builder newLayoutRegions( final LayoutRegions source )
    {
        return new Builder( source );
    }

    public LayoutRegions copy()
    {
        return LayoutRegions.newLayoutRegions( this ).build();
    }

    public static class Builder
        extends AbstractRegions.Builder<Builder>
    {
        private Builder( final LayoutRegions source )
        {
            super( source );
        }

        private Builder()
        {
            // Default
        }

        public LayoutRegions build()
        {
            return new LayoutRegions( this );
        }
    }
}


