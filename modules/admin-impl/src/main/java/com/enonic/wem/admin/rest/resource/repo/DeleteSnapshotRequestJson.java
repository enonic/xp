package com.enonic.wem.admin.rest.resource.repo;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteSnapshotRequestJson
{
    private final List<String> snapshotNames;

    private final Instant before;

    @JsonCreator
    public DeleteSnapshotRequestJson( @JsonProperty("snapshotNames") final List<String> snapshotNames,
                                      @JsonProperty("before") final Instant before )
    {
        this.snapshotNames = snapshotNames;
        this.before = before;
    }

    public List<String> getSnapshotNames()
    {
        return snapshotNames;
    }

    public Instant getBefore()
    {
        return before;
    }
}
