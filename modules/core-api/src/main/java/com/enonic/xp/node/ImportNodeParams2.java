package com.enonic.xp.node;

public class ImportNodeParams2
{
    private final BinaryAttachments binaryAttachments;

    private final Node node;

    private final InsertManualStrategy insertManualStrategy;

    private final boolean dryRun;

    private ImportNodeParams2( Builder builder )
    {
        binaryAttachments = builder.binaryAttachments;
        node = builder.node;
        insertManualStrategy = builder.insertManualStrategy;
        dryRun = builder.dryRun;
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

    public boolean isDryRun()
    {
        return dryRun;
    }

    public static final class Builder
    {
        private BinaryAttachments binaryAttachments;

        private Node node;

        private InsertManualStrategy insertManualStrategy;

        private boolean dryRun;


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

        public Builder dryRun( boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public ImportNodeParams2 build()
        {
            return new ImportNodeParams2( this );
        }
    }
}
