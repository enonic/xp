package com.enonic.xp.node;

public class ImportNodeParams
{
    private final BinaryAttachments binaryAttachments;

    private final Node node;

    private final InsertManualStrategy insertManualStrategy;

    private final boolean importPermissions;

    private final boolean importPermissionsOnCreate;

    private final RefreshMode refresh;

    private ImportNodeParams( Builder builder )
    {
        binaryAttachments = builder.binaryAttachments;
        node = builder.node;
        insertManualStrategy = builder.insertManualStrategy;
        importPermissions = builder.importPermissions;
        importPermissionsOnCreate = builder.importPermissionsOnCreate;
        this.refresh = builder.refresh;
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

    @Deprecated( since = "8" )
    public boolean isDryRun()
    {
        return false;
    }

    public boolean isImportPermissions()
    {
        return importPermissions;
    }

    public boolean isImportPermissionsOnCreate()
    {
        return importPermissionsOnCreate;
    }

    public RefreshMode getRefresh()
    {
        return refresh;
    }

    public static final class Builder
    {
        private BinaryAttachments binaryAttachments;

        private Node node;

        private InsertManualStrategy insertManualStrategy;

        private boolean importPermissions;

        private boolean importPermissionsOnCreate = true;

        private RefreshMode refresh;

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

        @Deprecated( since = "8" )
        public Builder dryRun( boolean dryRun )
        {
            throw new UnsupportedOperationException( "dryRun is not supported" );
        }

        public Builder importPermissions( boolean importPermissions )
        {
            this.importPermissions = importPermissions;
            return this;
        }

        public Builder importPermissionsOnCreate( boolean importPermissionsOnCreate )
        {
            this.importPermissionsOnCreate = importPermissionsOnCreate;
            return this;
        }

        public Builder refresh( final RefreshMode refresh )
        {
            this.refresh = refresh;
            return this;
        }

        public ImportNodeParams build()
        {
            return new ImportNodeParams( this );
        }
    }
}
