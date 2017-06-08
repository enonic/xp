package com.enonic.xp.repo.impl.node;

import java.time.Instant;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionMetadata;

public class ImportNodeVersionCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final NodePath nodePath;

    private final Instant timestamp;

    private final NodeVersion nodeVersion;

    private ImportNodeVersionCommand( final Builder builder )
    {
        super( builder );
        nodeVersionMetadata = builder.nodeVersionMetadata;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public void execute()
    {
        this.nodeStorageService.storeVersion( nodeVersionMetadata;
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeVersionMetadata nodeVersionMetadata;

        private Builder()
        {
        }

        public Builder nodeVersionMetadata( final NodeVersionMetadata val )
        {
            nodeVersionMetadata = val;
            return this;
        }

        public ImportNodeVersionCommand build()
        {
            return new ImportNodeVersionCommand( this );
        }
    }
}
