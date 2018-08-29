package com.enonic.xp.lib.content;

import com.google.common.collect.ImmutableSortedSet;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ActiveContentVersionEntry;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.GetActiveContentVersionsParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.lib.content.mapper.ContentVersionMapper;

public class GetActiveVersionHandler
    extends BaseVersionHandler
{
    @Override
    protected ContentVersionMapper doExecute()
    {
        final ContentId contentId = getContentId();
        if ( contentId == null )
        {
            return null;
        }
        else
        {
            final Branch currentBranch = ContextAccessor.current().getBranch();
            final GetActiveContentVersionsParams params = GetActiveContentVersionsParams.create().
                contentId( contentId ).
                branches( Branches.from( currentBranch ) ).
                build();
            ImmutableSortedSet<ActiveContentVersionEntry> activeContentVersions =
                this.contentService.getActiveVersions( params ).getActiveContentVersions();

            if ( activeContentVersions.isEmpty() )
            {
                return null;
            }
            ActiveContentVersionEntry activeContentVersionEntry = activeContentVersions.first();
            return new ContentVersionMapper( activeContentVersionEntry.getContentVersion() );
        }
    }
}
