package com.enonic.xp.export;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.core.internal.FileNames;
import com.enonic.xp.node.NodePath;

@PublicApi
public final class ExportNodesParams
{
    private final String exportName;

    private final NodePath sourceNodePath;

    private final boolean includeNodeIds;

    private final boolean includeVersions;

    private final boolean archive;

    private final NodeExportListener nodeExportListener;

    private ExportNodesParams( Builder builder )
    {
        this.exportName = builder.exportName;
        this.sourceNodePath = builder.sourceNodePath;
        this.includeNodeIds = builder.includeNodeIds;
        this.includeVersions = builder.includeVersions;
        this.archive = builder.archive;
        this.nodeExportListener = builder.nodeExportListener;
    }

    public static Builder create()
    {
        return new Builder();
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

    public boolean isArchive()
    {
        return archive;
    }

    public NodeExportListener getNodeExportListener()
    {
        return nodeExportListener;
    }

    public static final class Builder
    {
        private String exportName;

        private NodePath sourceNodePath;

        private boolean includeNodeIds = true;

        private boolean includeVersions = false;

        private boolean archive = false;

        private NodeExportListener nodeExportListener;

        private Builder()
        {
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

        public Builder archive( final boolean archive )
        {
            this.archive = archive;
            return this;
        }

        public Builder nodeExportListener( final NodeExportListener nodeExportListener )
        {
            this.nodeExportListener = nodeExportListener;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( sourceNodePath, "sourceNodePath is required" );
            Preconditions.checkArgument( exportName != null, "exportName is required" );
            Preconditions.checkArgument( FileNames.isSafeFileName( exportName ), "Invalid export name" );
        }

        public ExportNodesParams build()
        {
            this.validate();
            return new ExportNodesParams( this );
        }
    }
}
