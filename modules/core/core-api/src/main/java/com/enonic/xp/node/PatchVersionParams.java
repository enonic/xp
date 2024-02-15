package com.enonic.xp.node;

public final class PatchVersionParams
{
    private final NodeEditor editor;

    private final NodeVersionId versionId;

    private final NodeId nodeId;


    private PatchVersionParams( final Builder builder )
    {
        editor = builder.editor;
        versionId = builder.versionId;
        nodeId = builder.nodeId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeEditor getEditor()
    {
        return editor;
    }

    public NodeVersionId getVersionId()
    {
        return versionId;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public static final class Builder
    {
        private NodeEditor editor;

        private NodeVersionId versionId;

        private NodeId nodeId;

        private Builder()
        {
        }

        public Builder editor( final NodeEditor val )
        {
            editor = val;
            return this;
        }

        public Builder versionId( final NodeVersionId val )
        {
            versionId = val;
            return this;
        }

        public Builder nodeId( final NodeId val )
        {
            nodeId = val;
            return this;
        }

        public PatchVersionParams build()
        {
            return new PatchVersionParams( this );
        }
    }
}
