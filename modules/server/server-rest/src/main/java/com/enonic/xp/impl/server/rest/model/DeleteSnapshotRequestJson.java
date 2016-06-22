package com.enonic.xp.impl.server.rest.model;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteSnapshotRequestJson
{
    private final List<String> snapshotNames;

    private final String before;

    @JsonCreator
    public DeleteSnapshotRequestJson( @JsonProperty("snapshotNames") final List<String> snapshotNames,
                                      @JsonProperty("before") final String before )
    {
        this.snapshotNames = snapshotNames != null ? snapshotNames : Collections.emptyList();
        this.before = before;
    }

    public List<String> getSnapshotNames()
    {
        return snapshotNames;
    }

    public String getBefore()
    {
        return before;
    }
}
