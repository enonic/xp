package com.enonic.wem.api.export;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.vfs.VirtualFile;

public class ImportNodesParams
{
    private final NodePath importRootPath;

    private final boolean dryRun;

    private final VirtualFile exportRoot;

    private ImportNodesParams( final Builder builder )
    {
        this.importRootPath = builder.importRootPath;
        this.exportRoot = builder.exportRoot;
        this.dryRun = builder.dryRun;
    }

    public NodePath getImportRootPath()
    {
        return importRootPath;
    }

    public VirtualFile getExportRoot()
    {
        return exportRoot;
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
        private NodePath importRootPath;

        private boolean dryRun = false;

        private VirtualFile exportRoot;

        private Builder()
        {
        }

        public Builder importRootPath( NodePath importRootPath )
        {
            this.importRootPath = importRootPath;
            return this;
        }

        public Builder dryRun( boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public Builder exportRoot( final VirtualFile exportRoot )
        {
            this.exportRoot = exportRoot;
            return this;
        }

        public ImportNodesParams build()
        {
            return new ImportNodesParams( this );
        }
    }
}
