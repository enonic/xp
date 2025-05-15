package com.enonic.xp.content;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class DuplicateContentParams
{
    private final ContentId contentId;

    private final WorkflowInfo workflowInfo;

    private final DuplicateContentListener duplicateContentListener;

    private final Boolean includeChildren;

    private final boolean variant;

    private final String name;

    private final ContentPath parent;

    public DuplicateContentParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.workflowInfo = builder.workflowInfo;
        this.duplicateContentListener = builder.duplicateContentListener;
        this.includeChildren = !builder.variant ? builder.includeChildren : false;
        this.variant = builder.variant;
        this.name = builder.name;
        this.parent = builder.parent;
    }

    public static DuplicateContentParams.Builder create()
    {
        return new DuplicateContentParams.Builder();
    }

    public ContentId getContentId()
    {
        return contentId;
    }

    public DuplicateContentListener getDuplicateContentListener()
    {
        return duplicateContentListener;
    }

    public WorkflowInfo getWorkflowInfo()
    {
        return workflowInfo;
    }

    public Boolean getIncludeChildren()
    {
        return includeChildren;
    }

    public boolean isVariant()
    {
        return variant;
    }

    public String getName()
    {
        return name;
    }

    public ContentPath getParent()
    {
        return parent;
    }

    public static final class Builder
    {

        private ContentId contentId;

        private WorkflowInfo workflowInfo;

        private DuplicateContentListener duplicateContentListener;

        private Boolean includeChildren = true;

        private boolean variant;

        private String name;

        private ContentPath parent;

        private Builder()
        {
        }

        public Builder contentId( ContentId contentId )
        {
            this.contentId = contentId;
            return this;
        }

        public Builder workflowInfo( final WorkflowInfo workflowInfo )
        {
            this.workflowInfo = workflowInfo;
            return this;
        }

        public Builder duplicateContentListener( DuplicateContentListener duplicateContentListener )
        {
            this.duplicateContentListener = duplicateContentListener;
            return this;
        }

        public Builder includeChildren( final Boolean includeChildren )
        {
            this.includeChildren = Objects.requireNonNullElse( includeChildren, true );
            return this;
        }

        public Builder variant( final boolean variant )
        {
            this.variant = variant;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder parent( final ContentPath parent )
        {
            this.parent = parent;
            return this;
        }

        public DuplicateContentParams build()
        {
            Preconditions.checkNotNull( this.contentId, "Content id cannot be null" );
            return new DuplicateContentParams( this );
        }
    }
}
