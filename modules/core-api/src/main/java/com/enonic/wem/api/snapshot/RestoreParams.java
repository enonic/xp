package com.enonic.wem.api.snapshot;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class RestoreParams
{
    private final String snapshotName;

    private RestoreParams( Builder builder )
    {
        snapshotName = builder.snapshotName;
    }

    public String getSnapshotName()
    {
        return snapshotName;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String snapshotName;

        private Builder()
        {
        }

        public Builder snapshotName( String snapshotName )
        {
            this.snapshotName = snapshotName;
            return this;
        }

        private void validate()
        {
            Preconditions.checkArgument( !Strings.isNullOrEmpty( snapshotName ), "Snapshot name has to be given" );
        }


        public RestoreParams build()
        {
            return new RestoreParams( this );
        }
    }
}
