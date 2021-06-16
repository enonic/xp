package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.repository.RepositoryId;

public class SnapshotRequestJson
{
    private final RepositoryId repositoryId;

    private final boolean skipIndexedData;

    @JsonCreator
    public SnapshotRequestJson( @JsonProperty("repositoryId") final String repository,
                                @JsonProperty("skipIndexedData") final boolean skipIndexedData )
    {
        this.repositoryId = repository == null ? null : RepositoryId.from( repository );
        this.skipIndexedData = skipIndexedData;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public boolean isSkipIndexedData()
    {
        return skipIndexedData;
    }
}
