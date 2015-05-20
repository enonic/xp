package com.enonic.xp.content;

import com.google.common.annotations.Beta;

@Beta
public class ResolvePublishDependenciesResult
{

    private final PushedContentIdsWithInitialReason dependantsContentIds;

    private final PushedContentIdsWithInitialReason childrenContentsIds;

    private final PushedContentIdsWithInitialReason pushRequestedIds;

    private final Contents resolvedContent;

    private final CompareContentResults compareContentResults;

    private ResolvePublishDependenciesResult( final Builder builder )
    {
        this.dependantsContentIds = builder.dependantsContentIds;
        this.childrenContentsIds = builder.childrenContentsIds;
        this.pushRequestedIds = builder.pushRequestedIds;

        this.resolvedContent = builder.resolvedContent;
        this.compareContentResults = builder.compareContentResults;
    }

    public PushedContentIdsWithInitialReason getChildrenContentsIds()
    {
        return childrenContentsIds;
    }

    public PushedContentIdsWithInitialReason getDependantsContentIds()
    {
        return dependantsContentIds;
    }

    public PushedContentIdsWithInitialReason getPushRequestedIds()
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
        private PushedContentIdsWithInitialReason dependantsContentIds;

        private PushedContentIdsWithInitialReason childrenContentsIds;

        private PushedContentIdsWithInitialReason pushRequestedIds;

        private PushContentRequests pushContentRequests;

        private Contents resolvedContent = Contents.empty();

        private CompareContentResults compareContentResults;

        private Builder()
        {
        }

        private void buildDependantsAndChildrenIds()
        {
            dependantsContentIds = pushContentRequests.getDependantsContentIds( true, true );
            childrenContentsIds = pushContentRequests.getPushedBecauseChildOfContentIds( true, true );
            pushRequestedIds = pushContentRequests.getPushedBecauseRequestedContentIds( true );
        }

        public Builder pushContentRequests( final PushContentRequests pushContentRequests )
        {
            this.pushContentRequests = pushContentRequests;
            return this;
        }

        public Builder resolvedContent( final Contents resolvedContent )
        {
            this.resolvedContent = resolvedContent;
            return this;
        }

        public Builder compareContentResults( final CompareContentResults compareContentResults )
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
