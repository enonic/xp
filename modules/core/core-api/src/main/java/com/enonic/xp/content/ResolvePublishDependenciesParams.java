package com.enonic.xp.content;

import java.util.EnumSet;
import java.util.Set;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;

@PublicApi
public class ResolvePublishDependenciesParams
{
    private final ContentIds contentIds;

    private final Branch target;

    private final ContentIds excludedContentIds;

    private final ContentIds excludeChildrenIds;

    private final boolean excludeInvalid;

    private final EnumSet<WorkflowState> excludeWorkflowStates;


    private ResolvePublishDependenciesParams( Builder builder )
    {
        contentIds = builder.contentIds;
        target = builder.target;
        excludeChildrenIds = builder.excludeChildrenIds;
        excludedContentIds = builder.excludedContentIds;
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

    public ContentIds getExcludeChildrenIds()
    {
        return excludeChildrenIds;
    }

    public Branch getTarget()
    {
        return target;
    }

    public boolean isExcludeInvalid()
    {
        return excludeInvalid;
    }

    public EnumSet<WorkflowState> getExcludeWorkflowStates()
    {
        return excludeWorkflowStates;
    }

    public static final class Builder
    {
        private ContentIds contentIds;

        private ContentIds excludedContentIds;

        private ContentIds excludeChildrenIds;

        private Branch target;

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

        public Builder excludeInvalid( boolean excludeInvalid )
        {
            this.excludeInvalid = excludeInvalid;
            return this;
        }

        public Builder setExcludeWorkflowStates( Set<WorkflowState> workflowStates )
        {
            if ( !workflowStates.isEmpty() )
            {
                this.excludeWorkflowStates = EnumSet.copyOf( workflowStates );
            }
            return this;
        }

        public ResolvePublishDependenciesParams build()
        {
            return new ResolvePublishDependenciesParams( this );
        }
    }
}