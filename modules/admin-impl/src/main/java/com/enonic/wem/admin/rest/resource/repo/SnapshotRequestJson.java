package com.enonic.wem.admin.rest.resource.repo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class SnapshotRequestJson
{
    private String snapshotName;

    private boolean overwrite;

    @JsonCreator
    public SnapshotRequestJson( @JsonProperty("snapshotName") final String snapshotName, //
                                @JsonProperty("overwrite") final boolean overwrite )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( snapshotName ), "Snapshot name has to be given" );

        this.snapshotName = snapshotName.toLowerCase();
        this.overwrite = overwrite;
    }

    public String getSnapshotName()
    {
        return snapshotName;
    }

    public boolean isOverwrite()
    {
        return overwrite;
    }
}
