package com.enonic.xp.portal;

import java.util.List;

import com.google.common.collect.ImmutableListMultimap;

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

    public List<String> getContributions( final HtmlTag tag )
    {
        return this.contributions.get( tag );
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
        return source instanceof PortalResponse ? new Builder( (PortalResponse) source ) : new Builder( source );
    }

    public static Builder create( final PortalResponse source )
    {
        return new Builder( source );
    }

    public static class Builder
        extends WebResponse.Builder<Builder>
    {
        private boolean postProcess = true;

        private ImmutableListMultimap.Builder<HtmlTag, String> contributions = ImmutableListMultimap.builder();

        private boolean applyFilters = true;

        private Builder()
        {
        }

        private Builder( final WebResponse source )
        {
            super( source );
        }

        private Builder( final PortalResponse source )
        {
            super( source );
            this.postProcess = source.postProcess;
            this.contributions.putAll( source.contributions );
            this.applyFilters = source.applyFilters;
        }

        public Builder postProcess( final boolean postProcess )
        {
            this.postProcess = postProcess;
            return this;
        }

        public Builder contribution( final HtmlTag tag, final String value )
        {
            this.contributions.put( tag, value );
            return this;
        }

        public Builder contributionsFrom( final PortalResponse portalResponse )
        {
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
