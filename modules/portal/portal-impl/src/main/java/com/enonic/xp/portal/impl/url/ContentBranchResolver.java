package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;

final class ContentBranchResolver
{
    private final String branch;

    private final boolean preferSiteRequest;

    ContentBranchResolver( final Builder builder )
    {
        this.branch = builder.branch;
        this.preferSiteRequest = Objects.requireNonNullElse( builder.preferSiteRequest, true );
    }

    Branch resolve()
    {
        if ( branch != null )
        {
            return Branch.from( branch );
        }

        final PortalRequest portalRequest = PortalRequestAccessor.get();
        if ( preferSiteRequest && portalRequest != null && portalRequest.isSiteBase() )
        {
            return portalRequest.getBranch();
        }

        final Branch branch = ContextAccessor.current().getBranch();
        if ( branch != null )
        {
            return branch;
        }

        throw new IllegalArgumentException( "Branch not set" );
    }

    static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {
        private String branch;

        private Boolean preferSiteRequest;

        public Builder setBranch( final String branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder setPreferSiteRequest( final boolean preferSiteRequest )
        {
            this.preferSiteRequest = preferSiteRequest;
            return this;
        }

        ContentBranchResolver build()
        {
            return new ContentBranchResolver( this );
        }
    }
}
