package com.enonic.xp.region;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class LayoutRegions
    extends AbstractRegions
{
    private LayoutRegions( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final LayoutRegions source )
    {
        return new Builder( source );
    }

    @Override
    public LayoutRegions copy()
    {
        return LayoutRegions.create( this ).build();
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


