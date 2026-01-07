package com.enonic.xp.content;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Objects;

import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.project.ProjectName;

public final class ImportContentParams
{
    private final CreateAttachments attachments;

    private final Content content;

    private final ContentPath targetPath;

    private final EnumSet<ContentInheritType> inherit;

    private final ProjectName originProject;

    private final boolean importPermissions;

    private final boolean importPermissionsOnCreate;

    private ImportContentParams( Builder builder )
    {
        attachments = builder.attachments;
        content = builder.content;
        targetPath = builder.targetPath;
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

    public CreateAttachments getAttachments()
    {
        return attachments;
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
        private CreateAttachments attachments;

        private Content content;

        private ContentPath targetPath;

        private EnumSet<ContentInheritType> inherit;

        private ProjectName originProject;

        private boolean importPermissions;

        private boolean importPermissionsOnCreate;

        private Builder()
        {
        }

        public Builder attachments( CreateAttachments attachments )
        {
            this.attachments = attachments;
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
            Objects.requireNonNull( this.content, "content is required" );
            Objects.requireNonNull( this.targetPath, "targetPath is required" );
        }

        public ImportContentParams build()
        {
            validate();
            return new ImportContentParams( this );
        }
    }
}
