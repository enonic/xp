package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.annotation.JsonProperty;

final class RestoreSnapshotJsonRequest
    implements JsonRequest
{
    @JsonProperty("repository")
    @SuppressWarnings("unused")
    private String repositoryId;

    @JsonProperty("snapshotName")
    @SuppressWarnings("unused")
    private String snapshotName;

    public RestoreSnapshotJsonRequest repositoryId( final String repositoryId )
    {
        this.repositoryId = repositoryId;
        return this;
    }

    public RestoreSnapshotJsonRequest snapshotName( final String snapshotName )
    {
        this.snapshotName = snapshotName;
        return this;
    }
}
