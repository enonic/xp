package com.enonic.xp.lib.content;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.content.GetActiveContentVersionsParams;
import com.enonic.xp.content.GetActiveContentVersionsResult;
import com.enonic.xp.context.ContextAccessor;

public class GetActiveVersionsHandler
    extends BaseVersionHandler
{
    private Branches branches;

    public void setBranches( final String[] branches )
    {
        if ( branches == null )
        {
            final Branch currentBranch = ContextAccessor.current().getBranch();
            this.branches = Branches.from( currentBranch );
        }
        else
        {
            final Set<Branch> branchSet = Arrays.stream( branches ).map( Branch::from ).collect( Collectors.toSet() );
            this.branches = Branches.from( branchSet );
        }
    }

    @Override
    protected Object doExecute()
    {
        final ContentId contentId = getContentId();
        final GetActiveContentVersionsResult result;
        if ( contentId == null )
        {
            result = GetActiveContentVersionsResult.create().
                build();
        }
        else
        {
            final GetActiveContentVersionsParams params = GetActiveContentVersionsParams.create().
                contentId( contentId ).
                branches( this.branches ).
                build();
            result = this.contentService.getActiveVersions( params );
        }

        return new ActiveContentVersionsResultMapper( result );
    }
}
