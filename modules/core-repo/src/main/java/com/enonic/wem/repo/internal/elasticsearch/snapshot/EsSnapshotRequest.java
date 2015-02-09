package com.enonic.wem.repo.internal.elasticsearch.snapshot;

import java.util.Set;

public class EsSnapshotRequest
{
    private final Set<String> indices;

    private final boolean waitForCompletion;

    private EsSnapshotRequest( Builder builder )
    {
        indices = builder.indices;
        waitForCompletion = builder.waitForCompletion;
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Set<String> indices;

        private boolean waitForCompletion;

        private Builder()
        {
        }

        public Builder indices( Set<String> indices )
        {
            this.indices = indices;
            return this;
        }

        public Builder waitForCompletion( boolean waitForCompletion )
        {
            this.waitForCompletion = waitForCompletion;
            return this;
        }

        public EsSnapshotRequest build()
        {
            return new EsSnapshotRequest( this );
        }
    }
}
