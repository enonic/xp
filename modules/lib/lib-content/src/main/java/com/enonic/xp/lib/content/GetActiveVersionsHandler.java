package com.enonic.xp.lib.content;

import java.util.Arrays;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.GetActiveContentVersionsParams;
import com.enonic.xp.content.GetActiveContentVersionsResult;
import com.enonic.xp.lib.content.mapper.ActiveContentVersionsMapper;

public final class GetActiveVersionsHandler
    extends BaseContextHandler
{
    private String key;

    private String[] branches;

    @Override
    protected Object doExecute()
    {
        if ( branches == null )
        {
            throw new IllegalArgumentException( "Parameter 'branches' is required" );
        }

        final GetActiveContentVersionsParams params = GetActiveContentVersionsParams.create()
            .contentId( getContentId( key ) )
            .branches( Arrays.stream( branches ).map( Branch::from ).collect( Branches.collector() ) )
            .build();

        final GetActiveContentVersionsResult result = contentService.getActiveVersions( params );
        return new ActiveContentVersionsMapper( result );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setBranches( final String[] branches )
    {
        this.branches = branches;
    }
}
