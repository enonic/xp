package com.enonic.xp.core.export;

import com.google.common.base.Preconditions;

import com.enonic.xp.core.node.NodePath;

public class ExportNodesParams
{
    private final String targetDirectory;

    private final NodePath sourceNodePath;

    private final boolean dryRun;

    private final boolean includeNodeIds;

    private ExportNodesParams( Builder builder )
    {
        this.targetDirectory = builder.targetDirectory;
        this.sourceNodePath = builder.sourceNodePath;
        this.dryRun = builder.dryRun;
        this.includeNodeIds = builder.includeNodeIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getTargetDirectory()
    {
        return targetDirectory;
    }

    public NodePath getSourceNodePath()
    {
        return sourceNodePath;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }

    public static final class Builder
    {
        private String targetDirectory;

        private NodePath sourceNodePath;

        private boolean dryRun = false;

        private boolean includeNodeIds = true;

        private Builder()
        {
        }

        public Builder targetDirectory( final String exportName )
        {
            this.targetDirectory = exportName;
            return this;
        }

        public Builder sourceNodePath( final NodePath sourceNodePath )
        {
            this.sourceNodePath = sourceNodePath;
            return this;
        }

        public Builder dryRun( boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public Builder includeNodeIds( final boolean includeNodeIds )
        {
            this.includeNodeIds = includeNodeIds;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( targetDirectory );
            Preconditions.checkNotNull( sourceNodePath );
        }

        public ExportNodesParams build()
        {
            this.validate();
            return new ExportNodesParams( this );
        }
    }
}
