package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class ResolvePublishDependenciesResult
{

    private final Contents resolvedContent;

    private final ContentIds dependantsIdsResolvedWithChildrenIncluded;

    private final ContentIds dependantsIdsResolvedWithoutChildrenIncluded;

    private final ContentIds deletedDependantsIdsResolvedWithChildrenIncluded;

    private final ContentIds deletedDependantsIdsResolvedWithoutChildrenIncluded;

    private final ContentIds deletedChildrenContentsIds;

    private final ContentIds childrenContentsIds;

    private CompareContentResults compareContentResults;

    private ResolvePublishDependenciesResult( final Builder builder )
    {
        this.dependantsIdsResolvedWithChildrenIncluded = builder.dependantsIdsResolvedWithChildrenIncluded;
        this.dependantsIdsResolvedWithoutChildrenIncluded = builder.dependantsIdsResolvedWithoutChildrenIncluded;
        this.childrenContentsIds = builder.childrenContentsIds;

        this.deletedDependantsIdsResolvedWithChildrenIncluded = builder.deletedDependantsIdsResolvedWithChildrenIncluded;
        this.deletedDependantsIdsResolvedWithoutChildrenIncluded = builder.deletedDependantsIdsResolvedWithoutChildrenIncluded;
        this.deletedChildrenContentsIds = builder.deletedChildrenContentsIds;

        this.resolvedContent = builder.resolvedContent;
        this.compareContentResults = builder.compareContentResults;
    }

    public ContentIds getChildrenContentsIds()
    {
        return childrenContentsIds;
    }

    public ContentIds getDependantsIdsResolvedWithChildrenIncluded()
    {
        return dependantsIdsResolvedWithChildrenIncluded;
    }

    public ContentIds getDependantsIdsResolvedWithoutChildrenIncluded()
    {
        return dependantsIdsResolvedWithoutChildrenIncluded;
    }

    public Contents getResolvedContent()
    {
        return resolvedContent;
    }

    public ContentIds getDeletedDependantsIdsResolvedWithChildrenIncluded()
    {
        return deletedDependantsIdsResolvedWithChildrenIncluded;
    }

    public ContentIds getDeletedDependantsIdsResolvedWithoutChildrenIncluded()
    {
        return deletedDependantsIdsResolvedWithoutChildrenIncluded;
    }

    public ContentIds getDeletedChildrenContentsIds()
    {
        return deletedChildrenContentsIds;
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
        private ContentIds dependantsIdsResolvedWithChildrenIncluded;

        private ContentIds dependantsIdsResolvedWithoutChildrenIncluded;

        private ContentIds childrenContentsIds;

        private ContentIds deletedDependantsIdsResolvedWithChildrenIncluded;

        private ContentIds deletedDependantsIdsResolvedWithoutChildrenIncluded;

        private ContentIds deletedChildrenContentsIds;

        private PushContentRequests pushContentRequestsWithChildren;

        private PushContentRequests pushContentRequestsWithoutChildren;

        private Contents resolvedContent = Contents.empty();

        private CompareContentResults compareContentResults;

        private Builder()
        {
        }

        private void buildDependantsAndChildrenIds()
        {
            dependantsIdsResolvedWithoutChildrenIncluded = pushContentRequestsWithoutChildren.getDependantsContentIds( true );
            dependantsIdsResolvedWithChildrenIncluded = pushContentRequestsWithChildren.getDependantsContentIds( true );
            childrenContentsIds = pushContentRequestsWithChildren.getPushedBecauseChildOfContentIds( true );

            deletedDependantsIdsResolvedWithChildrenIncluded = pushContentRequestsWithoutChildren.getDeletedDependantsContentIds( true );
            deletedDependantsIdsResolvedWithoutChildrenIncluded = pushContentRequestsWithChildren.getDeletedDependantsContentIds( true );
            deletedChildrenContentsIds = pushContentRequestsWithChildren.getDeletedBecauseChildOfContentIds( true );
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
