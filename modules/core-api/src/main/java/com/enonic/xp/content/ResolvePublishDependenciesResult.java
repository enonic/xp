package com.enonic.xp.content;

import java.util.HashSet;
import java.util.Set;

import com.google.common.annotations.Beta;

@Beta
public class ResolvePublishDependenciesResult
{
    private final ContentIds dependantsIdsResolvedWithChildrenIncluded;

    private final ContentIds dependantsIdsResolvedWithoutChildrenIncluded;

    private final ContentIds childrenContentsIds;

    private ResolvePublishDependenciesResult( final Builder builder )
    {
        this.dependantsIdsResolvedWithChildrenIncluded = builder.dependantsIdsResolvedWithChildrenIncluded;
        this.dependantsIdsResolvedWithoutChildrenIncluded = builder.dependantsIdsResolvedWithoutChildrenIncluded;
        this.childrenContentsIds = builder.childrenContentsIds;
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

        private PushContentRequests pushContentRequestsWithChildren;

        private PushContentRequests pushContentRequestsWithoutChildren;

        private Builder()
        {
        }

        private void buildDependantsAndChildrenIds()
        {
            dependantsIdsResolvedWithoutChildrenIncluded = pushContentRequestsWithoutChildren.getDependantsContentIds( true );
            dependantsIdsResolvedWithChildrenIncluded = pushContentRequestsWithChildren.getDependantsContentIds( true );
            childrenContentsIds = pushContentRequestsWithChildren.getPushedBecauseChildOfContentIds( true );
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

        public ResolvePublishDependenciesResult build()
        {
            buildDependantsAndChildrenIds();
            return new ResolvePublishDependenciesResult( this );
        }
    }

}
