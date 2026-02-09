package com.enonic.xp.node;

public final class ImportNodeVersionParams
{
    private final Node node;

    private final NodeCommitId nodeCommitId;

    private final Attributes attributes;

    private ImportNodeVersionParams( final Builder builder )
    {
        node = builder.node;
        nodeCommitId = builder.nodeCommitId;
        attributes = builder.attributes;
    }

    public Node getNode()
    {
        return node;
    }

    public NodeCommitId getNodeCommitId()
    {
        return nodeCommitId;
    }

    public Attributes getAttributes()
    {
        return attributes;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Node node;

        private NodeCommitId nodeCommitId;

        private Attributes attributes;

        private Builder()
        {
        }

        public Builder node( final Node val )
        {
            node = val;
            return this;
        }

        public Builder nodeCommitId( final NodeCommitId val )
        {
            nodeCommitId = val;
            return this;
        }

        public Builder attributes( final Attributes val )
        {
            attributes = val;
            return this;
        }

        public ImportNodeVersionParams build()
        {
            return new ImportNodeVersionParams( this );
        }
    }
}
