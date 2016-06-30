package com.enonic.xp.impl.server.rest.model;

import java.time.Instant;
import java.util.Set;

import com.enonic.xp.node.SnapshotResult;

public class SnapshotResultJson
{
    private Set<String> indices;

    private String state;

    private String reason;

    private String name;

    private Instant timestamp;

    private SnapshotResultJson()
    {

    }

    public static SnapshotResultJson from( final SnapshotResult snapshotResult )
    {
        final SnapshotResultJson snapshotResultJson = new SnapshotResultJson();
        snapshotResultJson.indices = snapshotResult.getIndices();
        snapshotResultJson.name = snapshotResult.getName();
        snapshotResultJson.reason = snapshotResult.getReason();
        snapshotResultJson.state = snapshotResult.getState().toString();
        snapshotResultJson.timestamp = snapshotResult.getTimestamp();

        return snapshotResultJson;
    }

    public Set<String> getIndices()
    {
        return indices;
    }

    public String getState()
    {
        return state;
    }

    public String getReason()
    {
        return reason;
    }

    public String getName()
    {
        return name;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }
}
