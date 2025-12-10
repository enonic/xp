package com.enonic.xp.node;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class DuplicateNodeParams
{
    private final NodeId nodeId;

    private final NodeDataProcessor dataProcessor;

    private final DuplicateNodeListener duplicateListener;

    private final Boolean includeChildren;

    private final String name;

    private final NodePath parent;

    private final Attributes versionAttributes;

    private final RefreshMode refresh;

    private DuplicateNodeParams( Builder builder )
    {
        this.nodeId = builder.nodeId;
        this.dataProcessor = builder.dataProcessor;
        this.duplicateListener = builder.duplicateListener;
        this.includeChildren = builder.includeChildren;
        this.name = builder.name;
        this.parent = builder.parent;
        this.versionAttributes = builder.versionAttributes;
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

    public String getName()
    {
        return name;
    }

    public NodePath getParent()
    {
        return parent;
    }

    public Attributes getVersionAttributes()
    {
        return versionAttributes;
    }

    public RefreshMode getRefresh()
    {
        return refresh;
    }

    public static final class Builder
    {
        private NodeId nodeId;

        private NodeDataProcessor dataProcessor;

        private DuplicateNodeListener duplicateListener;

        private Boolean includeChildren = true;

        private String name;

        private NodePath parent;

        private Attributes versionAttributes;

        private RefreshMode refresh;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId nodeId )
        {
            this.nodeId = nodeId;
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
            this.includeChildren = Objects.requireNonNullElse( includeChildren, true );
            return this;
        }

        public Builder name( final String nodeName )
        {
            this.name = nodeName;
            return this;
        }

        public Builder parent( final NodePath parent )
        {
            this.parent = parent;
            return this;
        }

        public Builder versionAttributes( final Attributes versionAttributes )
        {
            this.versionAttributes = versionAttributes;
            return this;
        }

        public Builder refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( this.nodeId, "nodeId is required" );
        }

        public DuplicateNodeParams build()
        {
            this.validate();
            return new DuplicateNodeParams( this );
        }
    }
}
