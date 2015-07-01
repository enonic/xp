package com.enonic.xp.toolbox.repo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

final class ReindexJsonRequest
    implements JsonRequest
{

    @JsonProperty("repository")
    @SuppressWarnings("unused")
    private String repositoryId;

    @JsonProperty("initialize")
    @SuppressWarnings("unused")
    private boolean initialize;

    @JsonProperty("branches")
    @SuppressWarnings("unused")
    private List<String> branches;

    public ReindexJsonRequest repositoryId( final String repositoryId )
    {
        this.repositoryId = repositoryId;
        return this;
    }

    public ReindexJsonRequest initialize( final boolean initialize )
    {
        this.initialize = initialize;
        return this;
    }

    public ReindexJsonRequest branches( final List<String> branches )
    {
        this.branches = branches;
        return this;
    }
}
