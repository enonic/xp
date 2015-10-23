package com.enonic.xp.admin.impl.rest.resource.repo;

import java.time.Instant;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.ReindexResult;

public final class ReindexResultJson
{
    public String duration;

    public Instant startTime;

    public Instant endTime;

    public int numberReindexed;

    public List<String> branches;

    public String repositoryId;

    public static ReindexResultJson create( final ReindexResult result )
    {
        final ReindexResultJson json = new ReindexResultJson();
        json.duration = result.getDuration().toString();
        json.startTime = result.getStartTime();
        json.endTime = result.getEndTime();
        json.repositoryId = result.getRepositoryId().toString();
        json.numberReindexed = result.getReindexNodes().getSize();

        json.branches = Lists.newArrayList();
        for ( final Branch branch : result.getBranches() )
        {
            json.branches.add( branch.getName() );
        }

        return json;
    }
}
