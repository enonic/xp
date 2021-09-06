package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.project.ProjectName;

public final class ContentEventsSyncParams
{
    private final ContentId contentId;

    private final ProjectName sourceProject;

    private final ProjectName targetProject;

    private final ContentSyncEventType syncType;

    public ContentEventsSyncParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.sourceProject = builder.sourceProject;
        this.targetProject = builder.targetProject;
        this.syncType = builder.syncType;
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

    public ContentSyncEventType getSyncType()
    {
        return syncType;
    }

    public static final class Builder
    {
        private ContentId contentId;

        private ProjectName sourceProject;

        private ProjectName targetProject;

        private ContentSyncEventType syncType;

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

        public Builder syncEventType( ContentSyncEventType syncEventType )
        {
            this.syncType = syncEventType;
            return this;
        }

        public ContentEventsSyncParams build()
        {
            return new ContentEventsSyncParams( this );
        }

    }
}
