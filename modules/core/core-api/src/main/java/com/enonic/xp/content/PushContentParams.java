package com.enonic.xp.content;

import java.util.EnumSet;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public class PushContentParams
{
    private final ContentIds contentIds;

    private final ContentIds excludedContentIds;

    private final ContentIds excludeChildrenIds;

    private final Branch target;

    private final ContentPublishInfo contentPublishInfo;

    private final boolean includeChildren;

    private final boolean includeDependencies;

    private final PushContentListener publishContentListener;

    private final DeleteContentListener deleteContentListener;

    private final String message;

    private final boolean excludeInvalid;

    private final EnumSet<WorkflowState> excludeWorkflowStates;

    private PushContentParams( Builder builder )
    {
        contentIds = builder.contentIds;
        excludedContentIds = builder.excludedContentIds;
        target = builder.target;
        contentPublishInfo = builder.contentPublishInfo;
        includeDependencies = builder.includeDependencies;
        excludeChildrenIds = builder.excludeChildrenIds;
        includeChildren = builder.includeChildren;
        publishContentListener = builder.publishContentListener;
        deleteContentListener = builder.deleteContentListener;
        message = builder.message;
        excludeInvalid = builder.excludeInvalid;
        excludeWorkflowStates = builder.excludeWorkflowStates;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentIds getContentIds()
    {
        return contentIds;
    }

    public ContentIds getExcludedContentIds()
    {
        return excludedContentIds;
    }

    public Branch getTarget()
    {
        return target;
    }

    public ContentPublishInfo getContentPublishInfo()
    {
        return contentPublishInfo;
    }

    @Deprecated
    public boolean isIncludeChildren()
    {
        return includeChildren;
    }

    public ContentIds getExcludeChildrenIds()
    {
        return excludeChildrenIds;
    }

    public boolean isIncludeDependencies()
    {
        return includeDependencies;
    }

    public PushContentListener getPublishContentListener()
    {
        return publishContentListener;
    }

    public DeleteContentListener getDeleteContentListener()
    {
        return deleteContentListener;
    }

    public String getMessage()
    {
        return message;
    }

    public boolean isExcludeInvalid()
    {
        return excludeInvalid;
    }

    public EnumSet<WorkflowState> getExcludeWorkflowStates()
    {
        return excludeWorkflowStates;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final PushContentParams that = (PushContentParams) o;
        return includeChildren == that.includeChildren && includeDependencies == that.includeDependencies &&
            Objects.equals( excludeChildrenIds, that.excludeChildrenIds ) && Objects.equals( contentIds, that.contentIds ) &&
            Objects.equals( excludedContentIds, that.excludedContentIds ) && Objects.equals( target, that.target ) &&
            Objects.equals( publishContentListener, that.publishContentListener ) &&
            Objects.equals( deleteContentListener, that.deleteContentListener ) && Objects.equals( message, that.message ) &&
            Objects.equals( excludeWorkflowStates, that.excludeWorkflowStates ) && Objects.equals( excludeInvalid, that.excludeInvalid );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( contentIds, excludedContentIds, includeChildren, excludeChildrenIds, target, includeDependencies,
                             publishContentListener, deleteContentListener, message, excludeWorkflowStates, excludeInvalid );
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds;

        private ContentIds excludeChildrenIds = ContentIds.empty();

        private Branch target;

        private ContentPublishInfo contentPublishInfo;

        private boolean includeChildren = true;

        private boolean includeDependencies = true;

        private PushContentListener publishContentListener;

        private DeleteContentListener deleteContentListener;

        private String message;

        private boolean excludeInvalid = false;

        private EnumSet<WorkflowState> excludeWorkflowStates = EnumSet.noneOf( WorkflowState.class );

        private Builder()
        {
        }

        public Builder contentIds( ContentIds contentIds )
        {
            this.contentIds = contentIds;
            return this;
        }

        public Builder excludedContentIds( ContentIds excludedContentIds )
        {
            this.excludedContentIds = excludedContentIds;
            return this;
        }

        public Builder excludeChildrenIds( ContentIds excludeChildrenIds )
        {
            this.excludeChildrenIds = excludeChildrenIds;
            return this;
        }

        public Builder target( Branch target )
        {
            this.target = target;
            return this;
        }


        public Builder contentPublishInfo( ContentPublishInfo contentPublishInfo )
        {
            this.contentPublishInfo = contentPublishInfo;
            return this;
        }

        @Deprecated
        public Builder includeChildren( boolean includeChildren )
        {
            this.includeChildren = includeChildren;
            return this;
        }

        public Builder includeDependencies( final boolean includeDependencies )
        {
            this.includeDependencies = includeDependencies;
            return this;
        }

        public Builder pushListener( final PushContentListener publishContentListener )
        {
            this.publishContentListener = publishContentListener;
            return this;
        }

        public Builder deleteContentListener( final DeleteContentListener deleteContentListener )
        {
            this.deleteContentListener = deleteContentListener;
            return this;
        }

        public Builder message( final String message )
        {
            this.message = message;
            return this;
        }

        public Builder excludeInvalid( boolean excludeInvalid )
        {
            this.excludeInvalid = excludeInvalid;
            return this;
        }

        public Builder excludeWorkflowStates( EnumSet<WorkflowState> workflowStates )
        {
            this.excludeWorkflowStates = workflowStates;
            return this;
        }

        public PushContentParams build()
        {
            return new PushContentParams( this );
        }
    }
}
