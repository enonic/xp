package com.enonic.xp.core.impl.content;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.project.ProjectName;

public final class ContentEventsSyncParams
{
    private final List<ContentId> contentIds;

    private final ProjectName sourceProject;

    private final ProjectName targetProject;

    private final ContentSyncEventType syncType;

    public ContentEventsSyncParams( Builder builder )
    {
        this.contentIds = builder.contentIds.build();
        this.sourceProject = builder.sourceProject;
        this.targetProject = builder.targetProject;
        this.syncType = builder.syncType;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public List<ContentId> getContentIds()
    {
        return contentIds;
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
        private final ImmutableList.Builder<ContentId> contentIds = ImmutableList.builder();

        private ProjectName sourceProject;

        private ProjectName targetProject;

        private ContentSyncEventType syncType;

        public Builder addContentId( ContentId contentId )
        {
            this.contentIds.add( contentId );
            return this;
        }

        public Builder addContentIds( Collection<ContentId> contentIds )
        {
            this.contentIds.addAll( contentIds );
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
