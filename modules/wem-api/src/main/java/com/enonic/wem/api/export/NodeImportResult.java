package com.enonic.wem.api.export;

import com.enonic.wem.api.node.NodePaths;

public class NodeImportResult
{
    public final NodePaths importedNodes;

    private NodeImportResult( Builder builder )
    {
        importedNodes = builder.importedNodes;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private NodePaths importedNodes;

        private Builder()
        {
        }

        public Builder importedNodes( NodePaths importedNodes )
        {
            this.importedNodes = importedNodes;
            return this;
        }

        public NodeImportResult build()
        {
            return new NodeImportResult( this );
        }
    }
}
