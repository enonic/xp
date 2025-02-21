package com.enonic.xp.export;

import java.nio.file.Path;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.node.NodePath;

@PublicApi
public class ExportNodesParams
{
    private final String exportName;

    private final NodePath sourceNodePath;

    private final boolean dryRun;

    private final boolean includeNodeIds;

    private final boolean includeVersions;

    private final NodeExportListener nodeExportListener;

    private ExportNodesParams( Builder builder )
    {
        this.exportName = builder.exportName;
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

    @Deprecated
    public String getRootDirectory()
    {
        return null;
    }

    @Deprecated
    public String getTargetDirectory()
    {
        return null;
    }

    public String getExportName()
    {
        return exportName;
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
        private String exportName;

        private NodePath sourceNodePath;

        private boolean dryRun = false;

        private boolean includeNodeIds = true;

        private boolean includeVersions = false;

        private NodeExportListener nodeExportListener;

        private Builder()
        {
        }

        @Deprecated
        public Builder rootDirectory( final String rootDirectory )
        {
            return this;
        }

        @Deprecated
        public Builder targetDirectory( final String targetDirectory )
        {
            exportName = Path.of( targetDirectory ).getFileName().toString();
            return this;
        }

        public Builder exportName( final String exportName )
        {
            this.exportName = exportName;
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
            Preconditions.checkArgument( exportName != null, "exportName must be set" );
            Preconditions.checkArgument( FileNames.isSafeFileName( exportName ), "Invalid export name" );
            Preconditions.checkNotNull( sourceNodePath );
        }

        public ExportNodesParams build()
        {
            this.validate();
            return new ExportNodesParams( this );
        }
    }
}
