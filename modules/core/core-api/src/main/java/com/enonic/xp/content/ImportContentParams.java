package com.enonic.xp.content;

import java.util.Collection;
import java.util.EnumSet;

import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.InsertManualStrategy;

public class ImportContentParams
{
    private final BinaryAttachments binaryAttachments;

    private final Content content;

    private final ContentPath parentPath;

    private final InsertManualStrategy insertManualStrategy;

    private final EnumSet<ContentInheritType> inherit;

    private final boolean dryRun;

    private final boolean importPermissions;

    private ImportContentParams( Builder builder )
    {
        binaryAttachments = builder.binaryAttachments;
        content = builder.content;
        parentPath = builder.parentPath;
        insertManualStrategy = builder.insertManualStrategy;
        inherit = builder.inherit;
        dryRun = builder.dryRun;
        importPermissions = builder.importPermissions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Content getContent()
    {
        return content;
    }

    public ContentPath getParentPath()
    {
        return parentPath;
    }

    public InsertManualStrategy getInsertManualStrategy()
    {
        return insertManualStrategy;
    }

    public BinaryAttachments getBinaryAttachments()
    {
        return binaryAttachments;
    }

    public EnumSet<ContentInheritType> getInherit()
    {
        return inherit;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }

    public boolean isImportPermissions()
    {
        return importPermissions;
    }

    public static final class Builder
    {
        private BinaryAttachments binaryAttachments;

        private Content content;

        private ContentPath parentPath;

        private InsertManualStrategy insertManualStrategy;

        private EnumSet<ContentInheritType> inherit;

        private boolean dryRun;

        private boolean importPermissions;

        private Builder()
        {
        }

        public Builder binaryAttachments( BinaryAttachments binaryAttachments )
        {
            this.binaryAttachments = binaryAttachments;
            return this;
        }

        public Builder importContent( Content content )
        {
            this.content = content;
            return this;
        }

        public Builder parentPath( ContentPath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        public Builder insertManualStrategy( InsertManualStrategy insertManualStrategy )
        {
            this.insertManualStrategy = insertManualStrategy;
            return this;
        }

        public Builder inherit( Collection<ContentInheritType> inherit )
        {
            this.inherit =
                inherit != null ? !inherit.isEmpty() ? EnumSet.copyOf( inherit ) : EnumSet.noneOf( ContentInheritType.class ) : null;
            return this;
        }

        public Builder dryRun( boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public Builder importPermissions( boolean importPermissions )
        {
            this.importPermissions = importPermissions;
            return this;
        }

        public ImportContentParams build()
        {
            return new ImportContentParams( this );
        }
    }
}
