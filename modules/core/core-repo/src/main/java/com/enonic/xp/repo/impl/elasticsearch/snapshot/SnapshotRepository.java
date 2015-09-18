package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.nio.file.Path;

public class SnapshotRepository
{
    private final Path path;

    private final String name;

    private SnapshotRepository( Builder builder )
    {
        path = builder.path;
        name = builder.name;
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private Path path;

        private String name;

        private Builder()
        {
        }

        public Builder path( Path path )
        {
            this.path = path;
            return this;
        }

        public Builder name( String name )
        {
            this.name = name;
            return this;
        }

        public SnapshotRepository build()
        {
            return new SnapshotRepository( this );
        }
    }
}
