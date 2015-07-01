package com.enonic.xp.export;

import com.google.common.annotations.Beta;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.vfs.VirtualFile;

@Beta
public class ImportNodesParams
{
    private final NodePath targetNodePath;

    private final boolean dryRun;

    private final VirtualFile source;

    private final boolean importNodeids;

    private final boolean importPermissions;

    private ImportNodesParams( final Builder builder )
    {
        this.targetNodePath = builder.targetNodePath;
        this.source = builder.source;
        this.dryRun = builder.dryRun;
        this.importNodeids = builder.importNodeIds;
        this.importPermissions = builder.importPermissions;
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

    public boolean isImportPermissions()
    {
        return importPermissions;
    }

    public static final class Builder
    {
        private NodePath targetNodePath;

        private VirtualFile source;

        private boolean dryRun = false;

        private boolean importNodeIds;

        private boolean importPermissions;

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

        public Builder includePermissions( final boolean importPermissions )
        {
            this.importPermissions = importPermissions;
            return this;
        }

        public ImportNodesParams build()
        {
            return new ImportNodesParams( this );
        }
    }
}
