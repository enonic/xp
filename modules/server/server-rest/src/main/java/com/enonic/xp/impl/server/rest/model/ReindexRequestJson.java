package com.enonic.xp.impl.server.rest.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Splitter;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.repository.RepositoryId;

public final class ReindexRequestJson
{
    private RepositoryId repository;

    private boolean initialize;

    private Branches branches;

    @JsonCreator
    public ReindexRequestJson( @JsonProperty("repository") final String repository,
                                      @JsonProperty("branches") final String branches,
                                      @JsonProperty("initialize") final Boolean initialize)
    {
        this.repository = RepositoryId.from( repository );
        this.branches = parseBranches(branches);
        this.initialize = Boolean.TRUE.equals( initialize );
    }

    private static Branches parseBranches( final String branches )
    {
        final List<Branch> parsed = StreamSupport.stream( Splitter.on( "," ).split( branches ).spliterator(), false ).
            map( Branch::from ).collect( Collectors.toList() );
        return Branches.from( parsed );
    }

    public RepositoryId getRepository()
    {
        return repository;
    }

    public boolean isInitialize()
    {
        return initialize;
    }

    public Branches getBranches()
    {
        return branches;
    }
}
