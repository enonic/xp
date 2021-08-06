package com.enonic.xp.core.impl.content;

import java.util.EnumSet;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.project.ProjectName;

public final class ContentEventsSyncParams
{
    private final ContentId contentId;

    private final ProjectName sourceProject;

    private final ProjectName targetProject;

    private final EnumSet<ContentSyncEventType> syncTypes;

    public ContentEventsSyncParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.sourceProject = builder.sourceProject;
        this.targetProject = builder.targetProject;
        this.syncTypes = EnumSet.copyOf( builder.syncTypes.build() );
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

    public EnumSet<ContentSyncEventType> getSyncTypes()
    {
        return syncTypes;
    }

    public static final class Builder
    {
        private ContentId contentId;

        private ProjectName sourceProject;

        private ProjectName targetProject;

        private final ImmutableSet.Builder<ContentSyncEventType> syncTypes = ImmutableSet.builder();

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

        public Builder addSyncEventType( ContentSyncEventType syncEventType )
        {
            this.syncTypes.add( syncEventType );
            return this;
        }

        public ContentEventsSyncParams build()
        {
            return new ContentEventsSyncParams( this );
        }

    }
}
