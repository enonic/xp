package com.enonic.xp.export;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.util.BinaryReference;

@PublicApi
public class NodeImportResult
{
    public final NodePaths addedNodes;

    public final NodePaths updateNodes;

    private final boolean dryRun;

    private List<ImportError> importErrors = new ArrayList<>();

    private List<String> exportedBinaries = new ArrayList<>();

    private NodeImportResult( final Builder builder )
    {
        this.addedNodes = builder.addedNodes.build();
        this.updateNodes = builder.updatedNodes.build();
        this.importErrors = builder.importErrors;
        this.exportedBinaries = builder.exportedBinaries;
        this.dryRun = builder.dryRun;
    }

    public static Builder create()
    {
        return new Builder();
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

    public NodePaths getUpdateNodes()
    {
        return updateNodes;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }

    @Override
    public String toString()
    {
        return "NodeImportResult{" +
            "dryRun=" + dryRun +
            ", addedNodes=" + addedNodes +
            ", updateNodes=" + updateNodes +
            ", importErrors=" + Arrays.toString( importErrors.toArray() ) +
            ", exportedBinaries=" + Arrays.toString( exportedBinaries.toArray() ) +
            '}';
    }

    public static final class Builder
    {
        private final NodePaths.Builder addedNodes = NodePaths.create();

        private final NodePaths.Builder updatedNodes = NodePaths.create();

        private final List<String> exportedBinaries = new ArrayList<>();

        private final List<ImportError> importErrors = new ArrayList<>();

        private boolean dryRun = false;

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

        public Builder dryRun( final boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public NodeImportResult build()
        {
            return new NodeImportResult( this );
        }
    }

    public static class ImportError
    {
        private final String exception;

        private final String message;

        private final StackTraceElement[] stacktrace;

        public ImportError( final Exception exception, final String message )
        {
            this.exception = exception.toString();
            this.message = message;
            this.stacktrace = exception.getStackTrace();
        }

        public String getException()
        {
            return exception;
        }

        public String getMessage()
        {
            return message;
        }

        public StackTraceElement[] getStacktrace()
        {
            return stacktrace;
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
