package com.enonic.wem.api.export;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.node.NodePath;

public class ExportNodesParams
{
    private final String exportName;

    private final NodePath exportRoot;

    private final boolean dryRun;

    private final boolean includeNodeIds;

    private ExportNodesParams( Builder builder )
    {
        this.exportName = builder.exportName;
        this.exportRoot = builder.exportRoot;
        this.dryRun = builder.dryRun;
        this.includeNodeIds = builder.includeNodeIds;
    }

    public String getExportName()
    {
        return exportName;
    }

    public NodePath getExportRoot()
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
        private String exportName;

        private NodePath exportRoot;

        private boolean dryRun = false;

        private boolean includeNodeIds = true;

        private Builder()
        {
        }

        public Builder exportName( final String exportName )
        {
            this.exportName = exportName;
            return this;
        }

        public Builder exportRoot( final NodePath exportRoot )
        {
            this.exportRoot = exportRoot;
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
            Preconditions.checkNotNull( exportRoot );
        }

        public ExportNodesParams build()
        {
            return new ExportNodesParams( this );
        }
    }
}
