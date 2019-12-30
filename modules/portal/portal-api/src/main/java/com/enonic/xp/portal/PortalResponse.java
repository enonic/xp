package com.enonic.xp.portal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.web.WebResponse;

@PublicApi
public final class PortalResponse
    extends WebResponse
{
    private final boolean postProcess;

    private final ImmutableListMultimap<HtmlTag, String> contributions;

    private final boolean applyFilters;

    private PortalResponse( final Builder builder )
    {
        super( builder );
        this.postProcess = builder.postProcess;
        this.contributions = builder.contributions.build();
        this.applyFilters = builder.applyFilters;
    }

    public boolean isPostProcess()
    {
        return postProcess;
    }

    public ImmutableList<String> getContributions( final HtmlTag tag )
    {
        return this.contributions.containsKey( tag ) ? this.contributions.get( tag ) : ImmutableList.of();
    }

    public boolean hasContributions()
    {
        return !this.contributions.isEmpty();
    }

    public static Builder create()
    {
        return new Builder();
    }


    public boolean applyFilters()
    {
        return applyFilters;
    }

    public static Builder create( final WebResponse source )
    {
        return new Builder( source );
    }

    public static Builder create( final PortalResponse source )
    {
        return new Builder( source );
    }

    public static class Builder
        extends WebResponse.Builder<Builder>
    {
        private boolean postProcess = true;

        private ImmutableListMultimap.Builder<HtmlTag, String> contributions;

        private boolean applyFilters = true;

        private Builder()
        {
            clearContributions();
        }

        private Builder( final WebResponse source )
        {
            super( source );
            clearContributions();
        }

        private Builder( final PortalResponse source )
        {
            super( source );
            this.postProcess = source.postProcess;
            contributions( source.contributions );
            this.applyFilters = source.applyFilters;
        }

        public Builder postProcess( final boolean postProcess )
        {
            this.postProcess = postProcess;
            return this;
        }

        public Builder contributions( final ListMultimap<HtmlTag, String> contributions )
        {
            if ( this.contributions == null )
            {
                clearContributions();
            }
            this.contributions.putAll( contributions );
            return this;
        }

        public Builder contribution( final HtmlTag tag, final String value )
        {
            if ( this.contributions == null )
            {
                clearContributions();
            }
            this.contributions.put( tag, value );
            return this;
        }

        public Builder contributionsFrom( final PortalResponse portalResponse )
        {
            if ( this.contributions == null )
            {
                clearContributions();
            }
            this.contributions.putAll( portalResponse.contributions );
            return this;
        }

        public Builder clearContributions()
        {
            this.contributions = ImmutableListMultimap.builder();
            return this;
        }

        public Builder applyFilters( final boolean applyFilters )
        {
            this.applyFilters = applyFilters;
            return this;
        }

        @Override
        public PortalResponse build()
        {
            return new PortalResponse( this );
        }
    }
}
