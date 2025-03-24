package com.enonic.xp.portal.impl.url;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;

final class ContentBranchResolver
{
    private final String explicitBranch;

    ContentBranchResolver( final String explicitBranch )
    {
        this.explicitBranch = explicitBranch;
    }

    public Branch resolve()
    {
        if ( explicitBranch != null )
        {
            return Branch.from( explicitBranch );
        }

        final PortalRequest portalRequest = PortalRequestAccessor.get();
        if ( portalRequest != null && portalRequest.isSiteBase() )
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
}
