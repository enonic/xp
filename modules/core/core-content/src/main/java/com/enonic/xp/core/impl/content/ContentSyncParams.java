package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.project.ProjectName;

public final class ContentSyncParams
{
    private final ContentId contentId;

    private final ProjectName sourceProject;

    private final ProjectName targetProject;

    private final boolean includeChildren;

    public ContentSyncParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.sourceProject = builder.sourceProject;
        this.targetProject = builder.targetProject;
        this.includeChildren = builder.includeChildren;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public ProjectName getSourceProject()
    {
        return sourceProject;
    }

    public ProjectName getTargetProject()
    {
        return targetProject;
    }

    public boolean isIncludeChildren()
    {
        return includeChildren;
    }

    public static final class Builder
    {
        private ContentId contentId;

        private ProjectName sourceProject;

        private ProjectName targetProject;

        private boolean includeChildren = true;

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder sourceProject( ProjectName sourceProject )
        {
            this.sourceProject = sourceProject;
            return this;
        }

        public Builder targetProject( ProjectName targetProject )
        {
            this.targetProject = targetProject;
            return this;
        }

        public Builder includeChildren( boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( sourceProject, "sourceProject must be set." );
            Preconditions.checkNotNull( targetProject, "targetProject must be set." );
        }

        public ContentSyncParams build()
        {
            validate();
            return new ContentSyncParams( this );
        }

    }
}
