package com.enonic.wem.api.export;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodePaths;
import com.enonic.wem.api.util.BinaryReference;

public class NodeImportResult
{
    public final NodePaths addedNodes;

    public final NodePaths updateNodes;

    private List<ImportError> importErrors = Lists.newArrayList();

    private List<String> exportedBinaries = Lists.newArrayList();

    private NodeImportResult( final Builder builder )
    {
        addedNodes = builder.addedNodes.build();
        updateNodes = builder.updatedNodes.build();
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

    public NodePaths getAddedNodes()
    {
        return addedNodes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public String toString()
    {
        return "NodeImportResult{" +
            "addedNodes=" + addedNodes +
            ", updateNodes=" + updateNodes +
            ", importErrors=" + Arrays.toString( importErrors.toArray() ) +
            ", exportedBinaries=" + Arrays.toString( exportedBinaries.toArray() ) +
            '}';
    }

    public static final class Builder
    {
        private final NodePaths.Builder addedNodes = NodePaths.create();

        private final NodePaths.Builder updatedNodes = NodePaths.create();

        private final List<String> exportedBinaries = Lists.newArrayList();

        private final List<ImportError> importErrors = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder added( NodePath nodePath )
        {
            this.addedNodes.addNodePath( nodePath );
            return this;
        }

        public Builder updated( final NodePath nodePath )
        {
            this.updatedNodes.addNodePath( nodePath );
            return this;
        }

        public Builder addBinary( final String path, final BinaryReference binaryReference )
        {
            this.exportedBinaries.add( path + " [" + binaryReference + "]" );
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

        @Override
        public String toString()
        {
            return "ImportError{" +
                "exception=" + exception +
                ", message='" + message + '\'' +
                '}';
        }
    }


}
