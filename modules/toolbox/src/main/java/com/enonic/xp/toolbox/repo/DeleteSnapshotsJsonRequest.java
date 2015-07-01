package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.annotation.JsonProperty;

final class DeleteSnapshotsJsonRequest
    implements JsonRequest
{
    @JsonProperty("before")
    private String before;

    @JsonProperty("snapshotNames")
    private String[] snapshotNames;

    public DeleteSnapshotsJsonRequest beforeTimestamp( final String before )
    {
        this.before = before;
        return this;
    }

    public DeleteSnapshotsJsonRequest snapshotNames( final String[] snapshotNames )
    {
        this.snapshotNames = snapshotNames;
        return this;
    }
}
