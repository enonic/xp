package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.repository.RepositoryId;

public class SnapshotRequestJson
{
    private final String snapshotName;

    private final RepositoryId repositoryId;

    @JsonCreator
    public SnapshotRequestJson( @JsonProperty("snapshotName") final String snapshotName,
                                @JsonProperty("repositoryId") final String repository )
    {
        this.snapshotName = snapshotName;
        this.repositoryId = repository == null ? null : RepositoryId.from( repository );
    }

    public String getSnapshotName()
    {
        return snapshotName;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }
}
