package com.enonic.xp.repo.impl.dump.model;

import java.time.Instant;

import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.node.Attributes;

public record VersionMeta(NodeVersionId version, NodeVersionKey nodeVersionKey, NodePath nodePath, Instant timestamp,
                          NodeCommitId nodeCommitId, Attributes attributes)
{

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodePath nodePath;

        private Instant timestamp;

        private NodeVersionId version;

        private NodeVersionKey nodeVersionKey;

        private NodeCommitId nodeCommitId;

        private Attributes attributes;

        private Builder()
        {
        }

        public Builder nodePath( final NodePath val )
        {
            nodePath = val;
            return this;
        }

        public Builder timestamp( final Instant val )
        {
            timestamp = val;
            return this;
        }

        public Builder version( final NodeVersionId val )
        {
            version = val;
            return this;
        }

        public Builder nodeVersionKey( final NodeVersionKey val )
        {
            nodeVersionKey = val;
            return this;
        }

        public Builder nodeCommitId( final NodeCommitId val )
        {
            nodeCommitId = val;
            return this;
        }

        public Builder attributes( final Attributes val )
        {
            attributes = val;
            return this;
        }

        public VersionMeta build()
        {
            return new VersionMeta( version, nodeVersionKey, nodePath, timestamp, nodeCommitId, attributes );
        }
    }
}
