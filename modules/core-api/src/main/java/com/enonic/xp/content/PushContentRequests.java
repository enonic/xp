package com.enonic.xp.content;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Beta
public class PushContentRequests
{
    private final ImmutableSet<PushBecauseRequested> pushBecauseRequested;

    private final ImmutableSet<PushedBecauseReferredTo> pushedBecauseReferredTos;

    private final ImmutableSet<PushedBecauseParentOfPushed> pushedBecauseParentOfPusheds;

    private final ImmutableSet<PushedBecauseChildOfPushed> pushedBecauseChildOfPusheds;

    private final ImmutableSet<DeleteBecauseRequested> deleteBecauseRequested;

    private final ImmutableSet<DeletedBecauseReferredTo> deletedBecauseReferredTos;

    private final ImmutableSet<DeletedBecauseParentOfPushed> deletedBecauseParentOfPusheds;

    private final ImmutableSet<DeletedBecauseChildOfPushed> deletedBecauseChildOfPusheds;

    private final ImmutableMap<ContentId, ContentId> mapWithInitialReasonContentIds;

    private PushContentRequests( Builder builder )
    {
        pushBecauseRequested = ImmutableSet.copyOf( builder.pushBecauseRequested );
        pushedBecauseReferredTos = ImmutableSet.copyOf( builder.pushedBecauseReferredTo );
        pushedBecauseParentOfPusheds = ImmutableSet.copyOf( builder.pushedBecauseParentOf );
        pushedBecauseChildOfPusheds = ImmutableSet.copyOf( builder.pushedBecauseChildOf );

        deleteBecauseRequested = ImmutableSet.copyOf( builder.deleteBecauseRequested );
        deletedBecauseReferredTos = ImmutableSet.copyOf( builder.deletedBecauseReferredTo );
        deletedBecauseParentOfPusheds = ImmutableSet.copyOf( builder.deletedBecauseParentOf );
        deletedBecauseChildOfPusheds = ImmutableSet.copyOf( builder.deletedBecauseChildOf );

        mapWithInitialReasonContentIds = ImmutableMap.copyOf( builder.mapWithInitialReasonContentIds );
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

    public ContentId findContentIdThatInitiallyTriggeredPublish( ContentId id )
    {
        return mapWithInitialReasonContentIds.get( id );
    }

    public ContentIds getDependantsContentIds( boolean filterRequestedToPublishContentIds, boolean includeDeleted )
    {
        if ( includeDeleted )
        {
            return getResolvedContentIds( filterRequestedToPublishContentIds, PUSH_TYPE.PUSH_PARENT, PUSH_TYPE.PUSH_REF,
                                          PUSH_TYPE.DELETE_PARENT, PUSH_TYPE.DELETE_REF );
        }
        else
        {
            return getResolvedContentIds( filterRequestedToPublishContentIds, PUSH_TYPE.PUSH_PARENT, PUSH_TYPE.PUSH_REF );
        }
    }

    public ContentIds getDependenciesContentIds( boolean filterRequestedToPublishContentIds, boolean includeDeleted )
    {
        if ( includeDeleted )
        {
            return getResolvedContentIds( filterRequestedToPublishContentIds, PUSH_TYPE.PUSH_PARENT, PUSH_TYPE.PUSH_REF,
                                          PUSH_TYPE.DELETE_PARENT, PUSH_TYPE.DELETE_REF, PUSH_TYPE.PUSH_CHILD, PUSH_TYPE.DELETE_CHILD );
        }
        else
        {
            return getResolvedContentIds( filterRequestedToPublishContentIds, PUSH_TYPE.PUSH_PARENT, PUSH_TYPE.PUSH_REF,
                                          PUSH_TYPE.PUSH_CHILD );
        }
    }

    public ContentIds getChildrenContentIds( boolean filterRequestedToPublishContentIds, boolean includeDeleted )
    {
        if ( includeDeleted )
        {
            return getResolvedContentIds( filterRequestedToPublishContentIds, PUSH_TYPE.PUSH_CHILD, PUSH_TYPE.DELETE_CHILD );
        }
        else
        {
            return getResolvedContentIds( filterRequestedToPublishContentIds, PUSH_TYPE.PUSH_CHILD );
        }
    }

    public ContentIds getRequestedContentIds( boolean includeDeleted )
    {
        if ( includeDeleted )
        {
            return getResolvedContentIds( false, PUSH_TYPE.PUSH_REQUESTED, PUSH_TYPE.DELETE_REQUESTED );
        }
        else
        {
            return getResolvedContentIds( false, PUSH_TYPE.PUSH_REQUESTED );
        }
    }

    private ContentIds getResolvedContentIds( boolean filterRequestedToPublishContentIds, final  PUSH_TYPE... pushTypes )
    {
        Set<ContentId> ids = new HashSet<>();
        for ( PUSH_TYPE pushType : pushTypes )
        {
            switch ( pushType )
            {
                case PUSH_REQUESTED:
                    for ( PushBecauseRequested to : pushBecauseRequested )
                    {
                        ids.add( to.contentId );
                    }
                    break;
                case PUSH_REF:
                    for ( PushedBecauseReferredTo to : pushedBecauseReferredTos )
                    {
                        ids.add( to.contentId );
                    }
                    break;
                case PUSH_PARENT:
                    for ( PushedBecauseParentOfPushed to : pushedBecauseParentOfPusheds )
                    {
                        ids.add( to.contentId );
                    }
                    break;
                case PUSH_CHILD:
                    for ( PushedBecauseChildOfPushed to : pushedBecauseChildOfPusheds )
                    {
                        ids.add( to.contentId );
                    }
                    break;
                case DELETE_REQUESTED:
                    for ( DeleteBecauseRequested to : deleteBecauseRequested )
                    {
                        ids.add( to.contentId );
                    }
                    break;
                case DELETE_REF:
                    for ( DeletedBecauseReferredTo to : deletedBecauseReferredTos )
                    {
                        ids.add( to.contentId );
                    }
                    break;
                case DELETE_PARENT:
                    for ( DeletedBecauseParentOfPushed to : deletedBecauseParentOfPusheds )
                    {
                        ids.add( to.contentId );
                    }
                    break;
                case DELETE_CHILD:
                    for ( DeletedBecauseChildOfPushed to : deletedBecauseChildOfPusheds )
                    {
                        ids.add( to.contentId );
                    }
                    break;
                default:
                    break;
            }
        }

        if ( filterRequestedToPublishContentIds )
        {
            ids.removeAll( getRequestedContentIds( true ).getSet() );
        }

        return ContentIds.from( ids );
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

    public static class DeleteBecauseRequested
    {
        private final ContentId contentId;

        public DeleteBecauseRequested( final ContentId contentId )
        {
            this.contentId = contentId;
        }
    }

    public static class DeletedBecauseParentOfPushed
    {
        private final ContentId contentId;

        private final ContentId parentOf;

        public DeletedBecauseParentOfPushed( final ContentId contentId, final ContentId parentOf )
        {
            this.contentId = contentId;
            this.parentOf = parentOf;
        }
    }

    public static class DeletedBecauseChildOfPushed
    {
        private final ContentId contentId;

        private final ContentId childOf;

        public DeletedBecauseChildOfPushed( final ContentId contentId, final ContentId childOf )
        {
            this.contentId = contentId;
            this.childOf = childOf;
        }
    }

    public static class DeletedBecauseReferredTo
    {
        private final ContentId contentId;

        private final ContentId referredToBy;

        public DeletedBecauseReferredTo( final ContentId contentId, final ContentId referredToBy )
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

        private final Set<DeleteBecauseRequested> deleteBecauseRequested = Sets.newHashSet();

        private final Set<DeletedBecauseReferredTo> deletedBecauseReferredTo = Sets.newHashSet();

        private final Set<DeletedBecauseParentOfPushed> deletedBecauseParentOf = Sets.newHashSet();

        private final Set<DeletedBecauseChildOfPushed> deletedBecauseChildOf = Sets.newHashSet();

        private final Map<ContentId, ContentId> mapWithInitialReasonContentIds = Maps.newHashMap();

        private Builder()
        {
        }

        public Builder addRequested( final ContentId contentId, final ContentId initialReasonContentId )
        {
            this.pushBecauseRequested.add( new PushBecauseRequested( contentId ) );
            this.mapWithInitialReasonContentIds.put( contentId, initialReasonContentId );
            return this;
        }

        public Builder addParentOf( final ContentId contentId, final ContentId parentOf, final ContentId initialReasonContentId )
        {
            this.pushedBecauseParentOf.add( new PushedBecauseParentOfPushed( contentId, parentOf ) );
            this.mapWithInitialReasonContentIds.put( contentId, initialReasonContentId );
            return this;
        }

        public Builder addChildOf( final ContentId contentId, final ContentId childOf, final ContentId initialReasonContentId )
        {
            this.pushedBecauseChildOf.add( new PushedBecauseChildOfPushed( contentId, childOf ) );
            this.mapWithInitialReasonContentIds.put( contentId, initialReasonContentId );
            return this;
        }

        public Builder addReferredTo( final ContentId contentId, final ContentId referredTo, final ContentId initialReasonContentId )
        {
            this.pushedBecauseReferredTo.add( new PushedBecauseReferredTo( contentId, referredTo ) );
            this.mapWithInitialReasonContentIds.put( contentId, initialReasonContentId );
            return this;
        }

        public Builder addDeleteRequested( final ContentId contentId, final ContentId initialReasonContentId )
        {
            this.deleteBecauseRequested.add( new DeleteBecauseRequested( contentId ) );
            this.mapWithInitialReasonContentIds.put( contentId, initialReasonContentId );
            return this;
        }

        public Builder addDeleteBecauseParentOf( final ContentId contentId, final ContentId parentOf,
                                                 final ContentId initialReasonContentId )
        {
            this.deletedBecauseParentOf.add( new DeletedBecauseParentOfPushed( contentId, parentOf ) );
            this.mapWithInitialReasonContentIds.put( contentId, initialReasonContentId );
            return this;
        }

        public Builder addDeleteBecauseChildOf( final ContentId contentId, final ContentId childOf, final ContentId initialReasonContentId )
        {
            this.deletedBecauseChildOf.add( new DeletedBecauseChildOfPushed( contentId, childOf ) );
            this.mapWithInitialReasonContentIds.put( contentId, initialReasonContentId );
            return this;
        }

        public Builder addDeleteBecauseReferredTo( final ContentId contentId, final ContentId referredTo,
                                                   final ContentId initialReasonContentId )
        {
            this.deletedBecauseReferredTo.add( new DeletedBecauseReferredTo( contentId, referredTo ) );
            this.mapWithInitialReasonContentIds.put( contentId, initialReasonContentId );
            return this;
        }

        public Map<ContentId, ContentId> getMapWithInitialReasonContentIds()
        {
            return mapWithInitialReasonContentIds;
        }

        public PushContentRequests build()
        {
            return new PushContentRequests( this );
        }
    }

    public static enum PUSH_TYPE
    {
        PUSH_REQUESTED, PUSH_REF, PUSH_PARENT, PUSH_CHILD, DELETE_REQUESTED, DELETE_REF, DELETE_PARENT, DELETE_CHILD;
    }
}
