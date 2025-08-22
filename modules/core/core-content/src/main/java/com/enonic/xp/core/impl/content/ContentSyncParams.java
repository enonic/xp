package com.enonic.xp.core.impl.content;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.project.ProjectName;

public final class ContentSyncParams
{
    private final List<ContentId> contentIds;

    private final ProjectName sourceProject;

    private final ProjectName targetProject;

    private final boolean includeChildren;

    private ContentSyncParams( Builder builder )
    {
        this.contentIds = builder.contentIds.build();
        this.sourceProject = builder.sourceProject;
        this.targetProject = builder.targetProject;
        this.includeChildren = builder.includeChildren;
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

    public boolean isIncludeChildren()
    {
        return includeChildren;
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ContentId> contentIds = ImmutableList.builder();

        private ProjectName sourceProject;

        private ProjectName targetProject;

        private boolean includeChildren = true;

        public Builder addContentId( final ContentId contentId )
        {
            this.contentIds.add( contentId );
            return this;
        }

        public Builder addContentIds( final Collection<ContentId> contentIds )
        {
            this.contentIds.addAll( contentIds );
            return this;
        }

        public Builder sourceProject( final ProjectName sourceProject )
        {
            this.sourceProject = sourceProject;
            return this;
        }

        public Builder targetProject( final ProjectName targetProject )
        {
            this.targetProject = targetProject;
            return this;
        }

        public Builder includeChildren( final boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        private void validate()
        {
            Objects.requireNonNull( sourceProject, "sourceProject is required" );
            Objects.requireNonNull( targetProject, "targetProject is required" );
        }

        public ContentSyncParams build()
        {
            validate();
            return new ContentSyncParams( this );
        }

    }
}
