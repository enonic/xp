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

    private final RefreshMode refresh;

    private DuplicateNodeParams( Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.processor = builder.processor;
        this.dataProcessor = builder.dataProcessor;
        this.duplicateListener = builder.duplicateListener;
        this.includeChildren = builder.includeChildren;
        this.refresh = builder.refresh;
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

    public RefreshMode getRefresh()
    {
        return refresh;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private DuplicateNodeProcessor processor;

        private NodeDataProcessor dataProcessor;

        private DuplicateNodeListener duplicateListener;

        private Boolean includeChildren = true;

        private RefreshMode refresh;

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

        public Builder refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
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
