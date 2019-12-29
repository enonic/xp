package com.enonic.xp.export;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.node.NodePath;

@PublicApi
public class ExportNodesParams
{
    private final String rootDirectory;

    private final String targetDirectory;

    private final NodePath sourceNodePath;

    private final boolean dryRun;

    private final boolean includeNodeIds;

    private final boolean includeVersions;

    private final NodeExportListener nodeExportListener;

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
        this.includeVersions = builder.includeVersions;
        this.nodeExportListener = builder.nodeExportListener;
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

    public boolean isIncludeVersions()
    {
        return includeVersions;
    }

    public NodeExportListener getNodeExportListener()
    {
        return nodeExportListener;
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

        private boolean includeVersions = false;

        private NodeExportListener nodeExportListener;

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

        public Builder includeVersions( final boolean includeVersions )
        {
            this.includeVersions = includeVersions;
            return this;
        }

        public Builder nodeExportListener( final NodeExportListener nodeExportListener )
        {
            this.nodeExportListener = nodeExportListener;
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
