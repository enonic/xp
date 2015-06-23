package com.enonic.xp.node;

import java.time.Instant;

public class ImportNodeParams
{
    private final CreateNodeParams createNodeParams;

    private final Instant timestamp;

    private ImportNodeParams( Builder builder )
    {
        createNodeParams = builder.createNodeParams;
        timestamp = builder.timestamp;
    }

    public CreateNodeParams getCreateNodeParams()
    {
        return createNodeParams;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private CreateNodeParams createNodeParams;

        private Instant timestamp;

        private Builder()
        {
        }

        public Builder createNodeParams( CreateNodeParams createNodeParams )
        {
            this.createNodeParams = createNodeParams;
            return this;
        }

        public Builder timestamp( Instant timestamp )
        {
            this.timestamp = timestamp;
            return this;
        }

        public ImportNodeParams build()
        {
            return new ImportNodeParams( this );
        }
    }
}
