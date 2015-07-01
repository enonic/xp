package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.annotation.JsonProperty;

final class SnapshotJsonRequest
    implements JsonRequest
{
    @JsonProperty("repositoryId")
    @SuppressWarnings("unused")
    private String repositoryId;


    public SnapshotJsonRequest repositoryId( final String repositoryId )
    {
        this.repositoryId = repositoryId;
        return this;
    }

}
