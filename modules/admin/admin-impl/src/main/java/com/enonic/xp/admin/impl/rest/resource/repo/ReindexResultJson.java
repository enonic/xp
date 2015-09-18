package com.enonic.xp.admin.impl.rest.resource.repo;

import java.time.Instant;
import java.util.List;

public class ReindexResultJson
{
    private String duration;

    private Instant startTime;

    private Instant endTime;

    private int numberReindexed;

    private List<String> branches;

    private String repositoryId;


    public String getDuration()
    {
        return duration;
    }

    public void setDuration( final String duration )
    {
        this.duration = duration;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Instant getStartTime()
    {
        return startTime;
    }

    public void setStartTime( final Instant startTime )
    {
        this.startTime = startTime;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Instant getEndTime()
    {
        return endTime;
    }

    public void setEndTime( final Instant endTime )
    {
        this.endTime = endTime;
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getNumberReindexed()
    {
        return numberReindexed;
    }

    public void setNumberReindexed( final int numberReindexed )
    {
        this.numberReindexed = numberReindexed;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<String> getBranches()
    {
        return branches;
    }

    public void setBranches( final List<String> branches )
    {
        this.branches = branches;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getRepositoryId()
    {
        return repositoryId;
    }

    public void setRepositoryId( final String repositoryId )
    {
        this.repositoryId = repositoryId;
    }
}
