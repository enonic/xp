package com.enonic.wem.api.export;

import com.enonic.wem.api.node.NodePath;

public class ImportNodesParams
{
    private final NodePath targetNodePath;

    private final boolean dryRun;

    private final String sourceDirectory;

    private final boolean importNodeids;

    private ImportNodesParams( final Builder builder )
    {
        this.targetNodePath = builder.targetNodePath;
        this.sourceDirectory = builder.sourceDirectory;
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

    public String getSourceDirectory()
    {
        return sourceDirectory;
    }

    public boolean isImportNodeids()
    {
        return importNodeids;
    }

    public static final class Builder
    {
        private NodePath targetNodePath;

        private String sourceDirectory;

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

        public Builder sourceDirectory( final String sourceDirectory )
        {
            this.sourceDirectory = sourceDirectory;
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
