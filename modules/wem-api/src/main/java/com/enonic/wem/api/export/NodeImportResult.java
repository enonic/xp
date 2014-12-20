package com.enonic.wem.api.export;

import java.net.URL;
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
        private final NodePaths.Builder importedNodes = NodePaths.create();

        private final List<String> exportedBinaries = Lists.newArrayList();

        private final List<ImportError> importErrors = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder add( NodePath nodePath )
        {
            this.importedNodes.addNodePath( nodePath );
            return this;
        }

        public Builder addBinary( final URL url, final BinaryReference binaryReference )
        {
            this.exportedBinaries.add( url.toString() + " [" + binaryReference + "]" );
            return this;
        }

        public Builder addError( final Exception e )
        {
            this.importErrors.add( new ImportError( e, null ) );
            return this;
        }

        public Builder addError( final String message, final Exception e )
        {
            this.importErrors.add( new ImportError( e, message ) );
            return this;
        }


        public NodeImportResult build()
        {
            return new NodeImportResult( this );
        }
    }

    public static class ImportError
    {
        private final Exception exception;

        private final String message;

        public ImportError( final Exception exception, final String message )
        {
            this.exception = exception;
            this.message = message;
        }

        public Exception getException()
        {
            return exception;
        }

        public String getMessage()
        {
            return message;
        }
    }

}
