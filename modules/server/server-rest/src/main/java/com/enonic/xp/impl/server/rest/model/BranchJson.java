package com.enonic.xp.impl.server.rest.model;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

public final class BranchJson
{
    private final String name;

    private final List<SiteJson> sites;

    private BranchJson( Builder builder )
    {
        this.name = builder.name;
        this.sites = builder.sites.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getName()
    {
        return name;
    }

    public List<SiteJson> getSites()
    {
        return sites;
    }

    public static class Builder
    {
        final ImmutableList.Builder<SiteJson> sites = ImmutableList.builder();

        String name;

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder addSites( final Collection<SiteJson> sites )
        {
            this.sites.addAll( sites );
            return this;
        }

        public BranchJson build()
        {
            return new BranchJson( this );
        }
    }
}
