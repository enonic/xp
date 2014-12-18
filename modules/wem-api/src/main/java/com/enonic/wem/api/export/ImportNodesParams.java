package com.enonic.wem.api.export;

import com.enonic.wem.api.node.NodePath;

public class ImportNodesParams
{
    private final String exportName;

    private final NodePath importRootPath;

    private final boolean dryRun;

    private ImportNodesParams( Builder builder )
    {
        exportName = builder.exportName;
        importRootPath = builder.importRootPath;
        dryRun = builder.dryRun;
    }

    public String getExportName()
    {
        return exportName;
    }

    public NodePath getImportRootPath()
    {
        return importRootPath;
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

        private NodePath importRootPath;

        private boolean dryRun = false;

        private Builder()
        {
        }

        public Builder exportName( String exportName )
        {
            this.exportName = exportName;
            return this;
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

        public ImportNodesParams build()
        {
            return new ImportNodesParams( this );
        }
    }
}
