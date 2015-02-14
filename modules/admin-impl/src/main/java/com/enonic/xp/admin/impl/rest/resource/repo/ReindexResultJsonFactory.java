package com.enonic.xp.admin.impl.rest.resource.repo;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.branch.Branch;
import com.enonic.wem.api.index.ReindexResult;

class ReindexResultJsonFactory
{

    static ReindexResultJson create( final ReindexResult result )
    {
        final ReindexResultJson json = new ReindexResultJson();
        json.setDuration( result.getDuration().toString() );
        json.setStartTime( result.getStartTime() );
        json.setEndTime( result.getEndTime() );
        json.setBranches( resolveBranches( result ) );
        json.setRepositoryId( result.getRepositoryId().toString() );
        json.setNumberReindexed( result.getReindexNodes().getSize() );

        return json;
    }

    private static List<String> resolveBranches( final ReindexResult result )
    {
        List<String> branches = Lists.newArrayList();

        for ( final Branch branch : result.getBranches() )
        {
            branches.add( branch.getName() );
        }

        return branches;
    }
}
