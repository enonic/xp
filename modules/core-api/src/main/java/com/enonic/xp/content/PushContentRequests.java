package com.enonic.xp.content;

import java.util.HashSet;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@Beta
public class PushContentRequests
{
    private final ImmutableSet<PushBecauseRequested> pushBecauseRequested;

    private final ImmutableSet<PushedBecauseReferredTo> pushedBecauseReferredTos;

    private final ImmutableSet<PushedBecauseParentOfPushed> pushedBecauseParentOfPusheds;

    private final ImmutableSet<PushedBecauseChildOfPushed> pushedBecauseChildOfPusheds;

    private PushContentRequests( Builder builder )
    {
        pushBecauseRequested = ImmutableSet.copyOf( builder.pushBecauseRequested );
        pushedBecauseReferredTos = ImmutableSet.copyOf( builder.pushedBecauseReferredTo );
        pushedBecauseParentOfPusheds = ImmutableSet.copyOf( builder.pushedBecauseParentOf );
        pushedBecauseChildOfPusheds = ImmutableSet.copyOf( builder.pushedBecauseChildOf );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ImmutableSet<PushBecauseRequested> getPushBecauseRequested()
    {
        return pushBecauseRequested;
    }

    public ImmutableSet<PushedBecauseReferredTo> getPushedBecauseReferredTos()
    {
        return pushedBecauseReferredTos;
    }

    public ImmutableSet<PushedBecauseParentOfPushed> getPushedBecauseParentOfPusheds()
    {
        return pushedBecauseParentOfPusheds;
    }

    public ImmutableSet<PushedBecauseChildOfPushed> getPushedBecauseChildOfPusheds()
    {
        return pushedBecauseChildOfPusheds;
    }

    public ContentIds getDependantsContentIds( boolean filterRequestedToPublishContentIds )
    {
        Set<ContentId> ids = new HashSet<>();
        for ( PushedBecauseReferredTo to : pushedBecauseReferredTos )
        {
            ids.add( to.contentId );
        }
        for ( PushedBecauseParentOfPushed to : pushedBecauseParentOfPusheds )
        {
            ids.add( to.contentId );
        }
        if ( filterRequestedToPublishContentIds )
        {
            ids.removeAll( getPushedBecauseRequestedContentIds().getSet() );
        }
        ContentIds result = ContentIds.from( ids );
        return result;
    }

    public ContentIds getPushedBecauseChildOfContentIds( boolean filterRequestedToPublishContentIds )
    {
        Set<ContentId> ids = new HashSet<>();
        for ( PushedBecauseChildOfPushed to : pushedBecauseChildOfPusheds )
        {
            ids.add( to.contentId );
        }
        if ( filterRequestedToPublishContentIds )
        {
            ids.removeAll( getPushedBecauseRequestedContentIds().getSet() );
        }
        ContentIds result = ContentIds.from( ids );
        return result;
    }

    public ContentIds getPushedBecauseRequestedContentIds()
    {
        Set<ContentId> ids = new HashSet<>();
        for ( PushBecauseRequested to : pushBecauseRequested )
        {
            ids.add( to.contentId );
        }
        ContentIds result = ContentIds.from( ids );
        return result;
    }

    public static class PushBecauseRequested
    {
        private final ContentId contentId;

        public PushBecauseRequested( final ContentId contentId )
        {
            this.contentId = contentId;
        }
    }

    public static class PushedBecauseParentOfPushed
    {
        private final ContentId contentId;

        private final ContentId parentOf;

        public PushedBecauseParentOfPushed( final ContentId contentId, final ContentId parentOf )
        {
            this.contentId = contentId;
            this.parentOf = parentOf;
        }
    }

    public static class PushedBecauseChildOfPushed
    {
        private final ContentId contentId;

        private final ContentId childOf;

        public PushedBecauseChildOfPushed( final ContentId contentId, final ContentId childOf )
        {
            this.contentId = contentId;
            this.childOf = childOf;
        }
    }

    public static class PushedBecauseReferredTo
    {
        private final ContentId contentId;

        private final ContentId referredToBy;

        public PushedBecauseReferredTo( final ContentId contentId, final ContentId referredToBy )
        {
            this.contentId = contentId;
            this.referredToBy = referredToBy;
        }
    }


    public static final class Builder
    {
        private final Set<PushBecauseRequested> pushBecauseRequested = Sets.newHashSet();

        private final Set<PushedBecauseReferredTo> pushedBecauseReferredTo = Sets.newHashSet();

        private final Set<PushedBecauseParentOfPushed> pushedBecauseParentOf = Sets.newHashSet();

        private final Set<PushedBecauseChildOfPushed> pushedBecauseChildOf = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder addRequested( final ContentId contentId )
        {
            this.pushBecauseRequested.add( new PushBecauseRequested( contentId ) );
            return this;
        }

        public Builder addParentOf( final ContentId contentId, final ContentId parentOf )
        {
            this.pushedBecauseParentOf.add( new PushedBecauseParentOfPushed( contentId, parentOf ) );
            return this;
        }

        public Builder addChildOf( final ContentId contentId, final ContentId childOf )
        {
            this.pushedBecauseChildOf.add( new PushedBecauseChildOfPushed( contentId, childOf ) );
            return this;
        }

        public Builder addReferredTo( final ContentId contentId, final ContentId referredTo )
        {
            this.pushedBecauseReferredTo.add( new PushedBecauseReferredTo( contentId, referredTo ) );
            return this;
        }

        public PushContentRequests build()
        {
            return new PushContentRequests( this );
        }
    }
}
