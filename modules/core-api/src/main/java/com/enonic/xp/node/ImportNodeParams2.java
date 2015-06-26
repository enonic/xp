package com.enonic.xp.node;

public class ImportNodeParams2
{
    private BinaryAttachments binaryAttachments;

    private Node node;

    private InsertManualStrategy insertManualStrategy;

    private ImportNodeParams2( Builder builder )
    {
        binaryAttachments = builder.binaryAttachments;
        node = builder.node;
        insertManualStrategy = builder.insertManualStrategy;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node getNode()
    {
        return node;
    }

    public InsertManualStrategy getInsertManualStrategy()
    {
        return insertManualStrategy;
    }

    public BinaryAttachments getBinaryAttachments()
    {
        return binaryAttachments;
    }


    public static final class Builder
    {
        private BinaryAttachments binaryAttachments;

        private Node node;

        private InsertManualStrategy insertManualStrategy;

        private Builder()
        {
        }

        public Builder binaryAttachments( BinaryAttachments binaryAttachments )
        {
            this.binaryAttachments = binaryAttachments;
            return this;
        }

        public Builder importNode( Node node )
        {
            this.node = node;
            return this;
        }

        public Builder insertManualStrategy( InsertManualStrategy insertManualStrategy )
        {
            this.insertManualStrategy = insertManualStrategy;
            return this;
        }

        public ImportNodeParams2 build()
        {
            return new ImportNodeParams2( this );
        }
    }
}
