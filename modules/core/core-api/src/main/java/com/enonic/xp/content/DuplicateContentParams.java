package com.enonic.xp.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.PrincipalKey;

@PublicApi
public final class DuplicateContentParams
{
    private final ContentId contentId;

    private PrincipalKey creator;

    private final WorkflowInfo workflowInfo;

    private final DuplicateContentListener duplicateContentListener;

    private final Boolean includeChildren;

    private final boolean variant;

    private final String name;

    private final ContentPath parent;

    private DuplicateContentParams( Builder builder )
    {
        this.contentId = builder.contentId;
        this.creator = builder.creator;
        this.workflowInfo = builder.workflowInfo;
        this.duplicateContentListener = builder.duplicateContentListener;
        this.includeChildren = builder.includeChildren;
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

    @Deprecated
    public DuplicateContentParams creator( final PrincipalKey creator )
    {
        this.creator = creator;
        return this;
    }

    public DuplicateContentListener getDuplicateContentListener()
    {
        return duplicateContentListener;
    }

    public PrincipalKey getCreator()
    {
        return creator;
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

    @Override
    public boolean equals( final Object o )
    {
        return super.equals( o );
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    public static final class Builder
    {

        private ContentId contentId;

        private PrincipalKey creator;

        private WorkflowInfo workflowInfo;

        private DuplicateContentListener duplicateContentListener;

        private Boolean includeChildren = true;

        private boolean variant = false;

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

        public Builder creator( PrincipalKey creator )
        {
            this.creator = creator;
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
            this.includeChildren = includeChildren;
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
