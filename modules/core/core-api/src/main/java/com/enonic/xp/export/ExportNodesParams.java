package com.enonic.xp.export;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.node.NodePath;

@Beta
public class ExportNodesParams
{
    private final String rootDirectory;

    private final String targetDirectory;

    private final NodePath sourceNodePath;

    private final boolean dryRun;

    private final boolean includeNodeIds;

    private ExportNodesParams( Builder builder )
    {
        if ( builder.rootDirectory == null )
        {
            this.rootDirectory = builder.targetDirectory;
        }
        else
        {
            this.rootDirectory = builder.rootDirectory;
        }
        this.targetDirectory = builder.targetDirectory;
        this.sourceNodePath = builder.sourceNodePath;
        this.dryRun = builder.dryRun;
        this.includeNodeIds = builder.includeNodeIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getRootDirectory()
    {
        return rootDirectory;
    }

    public String getTargetDirectory()
    {
        return targetDirectory;
    }

    public NodePath getSourceNodePath()
    {
        return sourceNodePath;
    }

    public boolean isIncludeNodeIds()
    {
        return includeNodeIds;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }

    public static final class Builder
    {
        private String rootDirectory;

        private String targetDirectory;

        private NodePath sourceNodePath;

        private boolean dryRun = false;

        private boolean includeNodeIds = true;

        private Builder()
        {
        }

        public Builder rootDirectory( final String rootDirectory )
        {
            this.rootDirectory = rootDirectory;
            return this;
        }

        public Builder targetDirectory( final String targetDirectory )
        {
            this.targetDirectory = targetDirectory;
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
