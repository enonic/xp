package com.enonic.xp.content;

import java.util.Collection;
import java.util.EnumSet;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.project.ProjectName;

public class ImportContentParams
{
    private final BinaryAttachments binaryAttachments;

    private final Content content;

    private final ContentPath targetPath;

    private final InsertManualStrategy insertManualStrategy;

    private final EnumSet<ContentInheritType> inherit;

    private final ProjectName originProject;

    private final boolean importPermissions;

    private final boolean importPermissionsOnCreate;

    private ImportContentParams( Builder builder )
    {
        binaryAttachments = builder.binaryAttachments;
        content = builder.content;
        targetPath = builder.targetPath;
        insertManualStrategy = builder.insertManualStrategy;
        inherit = builder.inherit;
        importPermissions = builder.importPermissions;
        importPermissionsOnCreate = builder.importPermissionsOnCreate;
        originProject = builder.originProject;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Content getContent()
    {
        return content;
    }

    public ContentPath getTargetPath()
    {
        return targetPath;
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

    public ProjectName getOriginProject()
    {
        return originProject;
    }

    public boolean isImportPermissions()
    {
        return importPermissions;
    }

    public boolean isImportPermissionsOnCreate()
    {
        return importPermissionsOnCreate;
    }

    public static final class Builder
    {
        private BinaryAttachments binaryAttachments;

        private Content content;

        private ContentPath targetPath;

        private InsertManualStrategy insertManualStrategy;

        private EnumSet<ContentInheritType> inherit;

        private ProjectName originProject;

        private boolean importPermissions;

        private boolean importPermissionsOnCreate;

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

        public Builder targetPath( ContentPath targetPath )
        {
            this.targetPath = targetPath;
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

        public Builder originProject( ProjectName originProject )
        {
            this.originProject = originProject;
            return this;
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

        private void validate()
        {
            Preconditions.checkNotNull( this.content, "content cannot be null" );
            Preconditions.checkNotNull( this.targetPath, "targetPath cannot be null" );
        }

        public ImportContentParams build()
        {
            validate();
            return new ImportContentParams( this );
        }
    }
}
