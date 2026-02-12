package com.enonic.xp.lib.content;

import java.util.Arrays;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentId;
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
        if ( key == null || key.isEmpty() )
        {
            throw new IllegalArgumentException( "Parameter 'key' is required" );
        }

        if ( branches == null || branches.length == 0 )
        {
            throw new IllegalArgumentException( "Parameter 'branches' is required" );
        }

        final Branches branchesValue =
            Arrays.stream( branches ).map( Branch::from ).collect( Branches.collector() );

        final GetActiveContentVersionsParams params = GetActiveContentVersionsParams.create()
            .contentId( ContentId.from( key ) )
            .branches( branchesValue )
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
