package com.enonic.wem.api.snapshot;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class SnapshotParams
{
    final String snapshotName;

    final boolean overwrite;

    private SnapshotParams( Builder builder )
    {
        snapshotName = builder.snapshotName;
        overwrite = builder.overwrite;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getSnapshotName()
    {
        return snapshotName;
    }

    public boolean isOverwrite()
    {
        return overwrite;
    }

    public static final class Builder
    {
        private String snapshotName;

        private boolean overwrite = true;

        private Builder()
        {
        }

        public Builder snapshotName( final String snapshotName )
        {
            this.snapshotName = snapshotName;
            return this;
        }

        public Builder overwrite( final boolean overwrite )
        {
            this.overwrite = overwrite;
            return this;
        }

        private void validate()
        {
            Preconditions.checkArgument( !Strings.isNullOrEmpty( snapshotName ), "Snapshot name has to be given" );
        }

        public SnapshotParams build()
        {
            validate();
            return new SnapshotParams( this );
        }
    }
}
