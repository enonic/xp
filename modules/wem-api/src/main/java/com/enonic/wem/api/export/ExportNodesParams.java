package com.enonic.wem.api.export;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.node.NodePath;

public class ExportNodesParams
{
    private final String exportName;

    private final NodePath exportRoot;

    private final boolean dryRun;

    private ExportNodesParams( Builder builder )
    {
        exportName = builder.exportName;
        exportRoot = builder.exportRoot;
        dryRun = builder.dryRun;
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

        private Builder()
        {
        }

        public Builder exportName( final String exportName )
        {
            this.exportName = exportName;
            return this;
        }

        public Builder exportRoot( NodePath exportRoot )
        {
            this.exportRoot = exportRoot;
            return this;
        }

        public Builder dryRun( boolean dryRun )
        {
            this.dryRun = dryRun;
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
