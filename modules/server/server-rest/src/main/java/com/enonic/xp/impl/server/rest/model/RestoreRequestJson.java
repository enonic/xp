package com.enonic.xp.impl.server.rest.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.repository.RepositoryId;

public class RestoreRequestJson
{
    private final RepositoryId repositoryId;

    private final boolean skipIndexedData;

    private final String snapshotName;

    private final boolean latest;

    @JsonCreator
    public RestoreRequestJson( @JsonProperty("repository") final String repository,
                               @JsonProperty("skipIndexedData") final boolean skipIndexedData,
                               @JsonProperty("snapshotName") final String snapshotName, @JsonProperty("latest") final boolean latest )
    {
        this.repositoryId = repository == null ? null : RepositoryId.from( repository );
        this.skipIndexedData = skipIndexedData;
        this.snapshotName = snapshotName;
        this.latest = latest;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public boolean isSkipIndexedData()
    {
        return skipIndexedData;
    }

    public String getSnapshotName()
    {
        return snapshotName;
    }

    public boolean isLatest()
    {
        return latest;
    }

}
