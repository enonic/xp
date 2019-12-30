package com.enonic.xp.export;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.util.BinaryReference;

@PublicApi
public class NodeExportResult
{
    private final boolean dryRun;

    private final NodePaths exportedNodes;

    private final List<ExportError> exportErrors;

    private final List<String> exportedBinaries;

    private NodeExportResult( final Builder builder )
    {
        dryRun = builder.dryRun;
        exportedNodes = NodePaths.from( builder.nodePaths );
        exportErrors = builder.exportErrors;
        exportedBinaries = builder.exportedBinaries;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }

    public NodePaths getExportedNodes()
    {
        return exportedNodes;
    }

    public List<ExportError> getExportErrors()
    {
        return exportErrors;
    }

    public List<String> getExportedBinaries()
    {
        return exportedBinaries;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public int size()
    {
        return exportedNodes.getSize();
    }

    @Override
    public String toString()
    {
        return "NodeExportResult{" +
            "dryRun=" + dryRun +
            ", exportedNodes=" + exportedNodes +
            ", exportErrors=" + Arrays.toString( exportErrors.toArray() ) +
            ", exportedBinaries=" + Arrays.toString( exportedBinaries.toArray() ) +
            '}';
    }

    public static final class Builder
    {
        private boolean dryRun;

        private final List<ExportError> exportErrors = new ArrayList<>();

        private final List<String> exportedBinaries = new ArrayList<>();

        private final Set<NodePath> nodePaths = new HashSet<>();


        private Builder()
        {
        }

        public Builder dryRun( boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public Builder addBinary( final NodePath nodePath, final BinaryReference binaryReference )
        {
            this.exportedBinaries.add( nodePath.toString() + "[" + binaryReference.toString() + "]" );
            return this;
        }

        public Builder addNodePath( final NodePath nodePath )
        {
            this.nodePaths.add( nodePath );
            return this;
        }

        public Builder addError( final ExportError error )
        {
            this.exportErrors.add( error );
            return this;
        }

        public NodeExportResult build()
        {
            return new NodeExportResult( this );
        }
    }

}
