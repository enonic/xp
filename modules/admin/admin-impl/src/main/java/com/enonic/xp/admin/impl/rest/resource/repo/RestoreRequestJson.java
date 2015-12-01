package com.enonic.xp.admin.impl.rest.resource.repo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.enonic.xp.repository.RepositoryId;

public class RestoreRequestJson
{
    private RepositoryId repositoryId;

    private boolean skipIndexedData;

    private String snapshotName;

    @JsonCreator
    public RestoreRequestJson( @JsonProperty("repository") final String repository,
                               @JsonProperty("skipIndexedData") final boolean skipIndexedData,
                               @JsonProperty("snapshotName") final String snapshotName )
    {
        this.repositoryId = repository == null ? null : RepositoryId.from( repository );
        this.skipIndexedData = skipIndexedData;
        this.snapshotName = snapshotName;
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
}
