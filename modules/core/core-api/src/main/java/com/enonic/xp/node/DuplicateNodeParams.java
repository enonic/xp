package com.enonic.xp.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class DuplicateNodeParams
{
    private final NodeId nodeId;

    private final DuplicateNodeProcessor processor;

    private final NodeDataProcessor dataProcessor;

    private final DuplicateNodeListener duplicateListener;

    private final Boolean includeChildren;

    private DuplicateNodeParams( Builder builder )
    {
        nodeId = builder.nodeId;
        processor = builder.processor;
        dataProcessor = builder.dataProcessor;
        duplicateListener = builder.duplicateListener;
        includeChildren = builder.includeChildren;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    @Deprecated
    public DuplicateNodeProcessor getProcessor()
    {
        return processor;
    }

    @Deprecated
    public NodeDataProcessor getDataProcessor()
    {
        return dataProcessor;
    }

    public DuplicateNodeListener getDuplicateListener()
    {
        return duplicateListener;
    }

    public Boolean getIncludeChildren()
    {
        return includeChildren;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private DuplicateNodeProcessor processor;

        private NodeDataProcessor dataProcessor;

        private DuplicateNodeListener duplicateListener;

        private Boolean includeChildren = true;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
            return this;
        }

        @Deprecated
        public Builder processor( final DuplicateNodeProcessor processor )
        {
            this.processor = processor;
            return this;
        }

        @Deprecated
        public Builder dataProcessor( final NodeDataProcessor dataProcessor )
        {
            this.dataProcessor = dataProcessor;
            return this;
        }

        public Builder duplicateListener( final DuplicateNodeListener duplicateListener )
        {
            this.duplicateListener = duplicateListener;
            return this;
        }

        public Builder includeChildren( final Boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.nodeId, "id cannot be null" );
        }

        public DuplicateNodeParams build()
        {
            this.validate();
            return new DuplicateNodeParams( this );
        }
    }
}
