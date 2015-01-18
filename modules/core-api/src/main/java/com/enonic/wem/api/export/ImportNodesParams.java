package com.enonic.wem.api.export;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.vfs.VirtualFile;

public class ImportNodesParams
{
    private final NodePath targetPath;

    private final boolean dryRun;

    private final VirtualFile source;

    private ImportNodesParams( final Builder builder )
    {
        this.targetPath = builder.targetPath;
        this.source = builder.source;
        this.dryRun = builder.dryRun;
    }

    public NodePath getTargetPath()
    {
        return targetPath;
    }

    public VirtualFile getSource()
    {
        return source;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodePath targetPath;

        private boolean dryRun = false;

        private VirtualFile source;

        private Builder()
        {
        }

        public Builder targetPath( NodePath targetPath )
        {
            this.targetPath = targetPath;
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

        public ImportNodesParams build()
        {
            return new ImportNodesParams( this );
        }
    }
}
