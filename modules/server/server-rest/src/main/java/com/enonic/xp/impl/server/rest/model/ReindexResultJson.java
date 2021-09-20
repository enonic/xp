package com.enonic.xp.impl.server.rest.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.impl.server.rest.ModelToStringHelper;
import com.enonic.xp.index.ReindexResult;

public final class ReindexResultJson
{
    public String duration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    public Instant startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
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

        json.branches = new ArrayList<>();
        for ( final Branch branch : result.getBranches() )
        {
            json.branches.add( branch.getValue() );
        }

        return json;
    }

    @Override
    public String toString()
    {
        return ModelToStringHelper.convertToString( this );
    }
}
