package com.enonic.wem.api.export;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodePaths;
import com.enonic.wem.api.util.BinaryReference;

public class NodeImportResult
{
    public final NodePaths importedNodes;

    private List<ImportError> importErrors = Lists.newArrayList();

    private List<String> exportedBinaries = Lists.newArrayList();

    private NodeImportResult( final Builder builder )
    {
        importedNodes = builder.importedNodes.build();
        this.importErrors = builder.importErrors;
        this.exportedBinaries = builder.exportedBinaries;
    }

    public List<ImportError> getImportErrors()
    {
        return importErrors;
    }

    public void setImportErrors( final List<ImportError> importErrors )
    {
        this.importErrors = importErrors;
    }

    public List<String> getExportedBinaries()
    {
        return exportedBinaries;
    }

    public void setExportedBinaries( final List<String> exportedBinaries )
    {
        this.exportedBinaries = exportedBinaries;
    }

    public NodePaths getImportedNodes()
    {
        return importedNodes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private NodePaths.Builder importedNodes = NodePaths.create();

        private List<String> exportedBinaries = Lists.newArrayList();

        private List<ImportError> importErrors = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder add( NodePath nodePath )
        {
            this.importedNodes.addNodePath( nodePath );
            return this;
        }

        public Builder addBinary( final String path, final BinaryReference binaryReference )
        {
            this.exportedBinaries.add( path + " [" + binaryReference + "]" );
            return this;
        }

        public Builder addError( final Exception e )
        {
            this.importErrors.add( new ImportError( e ) );
            return this;
        }

        public NodeImportResult build()
        {
            return new NodeImportResult( this );
        }
    }

    public static class ImportError
    {
        private Exception exception;

        public ImportError( final Exception exception )
        {
            this.exception = exception;
        }
    }
}
