package com.enonic.wem.admin.rest.resource.repo;

import java.util.Set;

import com.enonic.wem.api.snapshot.SnapshotResult;

public class SnapshotResultJson
{
    private Set<String> indices;

    private String state;

    private String reason;

    private String name;


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
}
