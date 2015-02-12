package com.enonic.wem.api.export;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.vfs.VirtualFile;

public class ImportNodesParams
{
    private final NodePath targetNodePath;

    private final boolean dryRun;

    private final VirtualFile source;

    private final boolean importNodeids;

    private ImportNodesParams( final Builder builder )
    {
        this.targetNodePath = builder.targetNodePath;
        this.source = builder.source;
        this.dryRun = builder.dryRun;
        this.importNodeids = builder.importNodeIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodePath getTargetNodePath()
    {
        return targetNodePath;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }

    public VirtualFile getSource()
    {
        return source;
    }

    public boolean isImportNodeids()
    {
        return importNodeids;
    }

    public static final class Builder
    {
        private NodePath targetNodePath;

        private VirtualFile source;

        private boolean dryRun = false;

        private boolean importNodeIds;

        private Builder()
        {
        }

        public Builder targetNodePath( NodePath targetNodePath )
        {
            this.targetNodePath = targetNodePath;
            return this;
        }

        public Builder dryRun( boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public Builder source( final VirtualFile source )
        {
            this.source = source;
            return this;
        }

        public Builder includeNodeIds( final boolean importNodeIds )
        {
            this.importNodeIds = importNodeIds;
            return this;
        }

        public ImportNodesParams build()
        {
            return new ImportNodesParams( this );
        }
    }
}
