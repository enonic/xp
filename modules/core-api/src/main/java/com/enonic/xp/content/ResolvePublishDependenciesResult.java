package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class ResolvePublishDependenciesResult
{

    private final PushedContentIdsWithReason dependantsIdsResolvedWithChildrenIncluded;

    private final PushedContentIdsWithReason dependantsIdsResolvedWithoutChildrenIncluded;

    private final PushedContentIdsWithReason childrenContentsIds;

    private final PushedContentIdsWithReason pushRequestedIds;

    private final Contents resolvedContent;

    private CompareContentResults compareContentResults;

    private ResolvePublishDependenciesResult( final Builder builder )
    {
        this.dependantsIdsResolvedWithChildrenIncluded = builder.dependantsIdsResolvedWithChildrenIncluded;
        this.dependantsIdsResolvedWithoutChildrenIncluded = builder.dependantsIdsResolvedWithoutChildrenIncluded;
        this.childrenContentsIds = builder.childrenContentsIds;
        this.pushRequestedIds = builder.pushRequestedIds;

        this.resolvedContent = builder.resolvedContent;
        this.compareContentResults = builder.compareContentResults;
    }

    public PushedContentIdsWithReason getChildrenContentsIds()
    {
        return childrenContentsIds;
    }

    public PushedContentIdsWithReason getDependantsIdsResolvedWithChildrenIncluded()
    {
        return dependantsIdsResolvedWithChildrenIncluded;
    }

    public PushedContentIdsWithReason getDependantsIdsResolvedWithoutChildrenIncluded()
    {
        return dependantsIdsResolvedWithoutChildrenIncluded;
    }

    public PushedContentIdsWithReason getPushRequestedIds()
    {
        return pushRequestedIds;
    }

    public Contents getResolvedContent()
    {
        return resolvedContent;
    }

    public CompareContentResults getCompareContentResults()
    {
        return compareContentResults;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        // dependants resolved with includeChildren=true might include nodes <b>referred</b> by <b>children</b>!
        private PushedContentIdsWithReason dependantsIdsResolvedWithChildrenIncluded;

        private PushedContentIdsWithReason dependantsIdsResolvedWithoutChildrenIncluded;

        private PushedContentIdsWithReason childrenContentsIds;

        private PushedContentIdsWithReason pushRequestedIds;

        private PushContentRequests pushContentRequestsWithChildren;

        private PushContentRequests pushContentRequestsWithoutChildren;

        private Contents resolvedContent = Contents.empty();

        private CompareContentResults compareContentResults;

        private Builder()
        {
        }

        private void buildDependantsAndChildrenIds()
        {
            dependantsIdsResolvedWithoutChildrenIncluded = pushContentRequestsWithoutChildren.getDependantsContentIds( true, true );
            dependantsIdsResolvedWithChildrenIncluded = pushContentRequestsWithChildren.getDependantsContentIds( true, true );
            childrenContentsIds = pushContentRequestsWithChildren.getPushedBecauseChildOfContentIds( true, true );
            pushRequestedIds = pushContentRequestsWithChildren.getPushedBecauseRequestedContentIds( true );
        }

        public Builder pushContentRequestsWithChildren( final PushContentRequests pushContentRequests )
        {
            this.pushContentRequestsWithChildren = pushContentRequests;
            return this;
        }

        public Builder pushContentRequestsWithoutChildren( final PushContentRequests pushContentRequests )
        {
            this.pushContentRequestsWithoutChildren = pushContentRequests;
            return this;
        }

        public Builder setResolvedContent( final Contents resolvedContent )
        {
            this.resolvedContent = resolvedContent;
            return this;
        }

        public Builder setCompareContentResults( final CompareContentResults compareContentResults )
        {
            this.compareContentResults = compareContentResults;
            return this;
        }

        public ResolvePublishDependenciesResult build()
        {
            buildDependantsAndChildrenIds();
            return new ResolvePublishDependenciesResult( this );
        }
    }

}
